package de.erichambuch.biproclient.auth;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import de.erichambuch.biproclient.AppInfo;
import de.erichambuch.biproclient.R;
import de.erichambuch.biproclient.auth.saml.BiproSAMLAuthentication;
import de.erichambuch.biproclient.auth.saml.MTan2FACommand;
import de.erichambuch.biproclient.auth.saml.Username2FACommand;
import de.erichambuch.biproclient.auth.sts.BiproTokenAuthentication;
import de.erichambuch.biproclient.auth.sts.UsernameLoginCommand;
import de.erichambuch.biproclient.bipro.base.CommandCallback;
import de.erichambuch.biproclient.main.provider.ProviderConfiguration;

/**
 * Führt und speichert die Authentifizierungsdaten für einen Businessservice.
 */
public class AuthenticationManager {

    private final ProviderConfiguration configuration;

    private BiproAuthentication authentication = null;

    public AuthenticationManager(ProviderConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Starte Authentifizierungs-Flow.
     *
     * @param context der Aufruf-Context
     * @param successCallback Callback für Ergebnis
     */
    public void startAuthenticate(final Context context, final Runnable successCallback) {
        if ( authentication == null || authentication.requiresReauthentication() ) {
            LayoutInflater li = LayoutInflater.from(context);
            final View loginView = li.inflate(R.layout.dialog_login, null);
            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(
                    context);
            alertDialogBuilder.setView(loginView);
            alertDialogBuilder
                    .setCancelable(false)
                    .setTitle("Anmeldung")
                    .setIcon(R.drawable.ic_lock_black_24dp)
                    .setPositiveButton(R.string.login,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    String userName = ((TextView) loginView.findViewById(R.id.username)).getText().toString().trim();
                                    String password = ((TextView) loginView.findViewById(R.id.password)).getText().toString().trim();
                                    if(configuration.getAuthMethode() == ProviderConfiguration.AuthMethode.SAML)
                                        AuthenticationManager.this.loginSAML(context, userName, password, configuration.getTgicProviderServiceId(), successCallback);
                                    else
                                        AuthenticationManager.this.loginSTS(context, userName, password, successCallback);
                                }
                            })
                    .setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            alertDialogBuilder.show();
        } else {
            // existing authentication can be reused
            successCallback.run();
        }
    }

    /**
     * Login mit User/Passwort bei STS des VU. Liefert ein BiPRO-Token.
     * @param context
     * @param user
     * @param pw
     * @param successCallback
     */
    private void loginSTS(Context context, String user, String pw, final Runnable successCallback) {
           new UsernameLoginCommand(configuration).execute(user, pw, new CommandCallback() {
                @Override
                public void onSuccess(final Object data) {
                    ContextCompat.getMainExecutor(context).execute(new Runnable() {
                        @Override
                        public void run() {
                            authentication = new BiproTokenAuthentication((BiproTokenAuthentication.BiproToken) data);
                            successCallback.run();
                        }
                    });

                }
                @Override
                public void onFailure(final Exception e) {
                    ContextCompat.getMainExecutor(context).execute(new Runnable() {
                        @Override
                        public void run() {
                            Log.e(AppInfo.APP_NAME, "Fehler Auth", e);
                            authentication = null;
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Fehler bei Authentifizierung");
                            builder.setMessage(e.getMessage());
                            builder.setIcon(R.drawable.ic_error_black_24dp);
                            builder.setNeutralButton(R.string.cancel, (dialog, which) -> {});
                            builder.create().show();
                        }
                    });
                }
            });
    }

    /**
     * Login mit SAML beim ISTS der TGIC. Benötigt durch 2FA noch eine zusätzliches Kriterium (mTAN).
     *
     * @param context
     * @param user
     * @param pw
     * @param serviceId
     * @param successCallback
     */
    private void loginSAML(final Context context, String user, String pw, final String serviceId, final Runnable successCallback) {
        new Username2FACommand(configuration).execute(user, pw, serviceId, new CommandCallback() {
            @Override
            public void onSuccess(final Object data) {
                ContextCompat.getMainExecutor(context).execute(new Runnable() {
                    @Override
                    public void run() {
                        requestMTan(context, (String) (((Object[])data)[0]), (String)(((Object[])data)[1]), successCallback);
                    }
                });
            }
            @Override
            public void onFailure(final Exception e) {
                ContextCompat.getMainExecutor(context).execute(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(AppInfo.APP_NAME, "Fehler 2FA", e);
                        authentication = null;
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Fehler bei Authentifizierung");
                        builder.setMessage(e.getMessage());
                        builder.setIcon(R.drawable.ic_error_black_24dp);
                        builder.setNeutralButton(R.string.cancel, (dialog, which) -> {});
                        builder.create().show();
                    }
                });
            }
        });
    }

    /**
     * mTAN in neuem Dialog anfordern.
     * @param context
     * @param requestId WS-Addressing
     * @param successCallback
     */
    private void requestMTan(final Context context, final String requestId, final String messageText, final Runnable successCallback) {
        final LayoutInflater li = LayoutInflater.from(context);
        final View loginView = li.inflate(R.layout.dialog_mtan, null);
        final MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(
                context);
        alertDialogBuilder.setView(loginView);
        alertDialogBuilder
                .setCancelable(false)
                .setTitle("2F-Anmeldung: "+messageText)
                .setIcon(R.drawable.ic_lock_black_24dp)
                .setPositiveButton(R.string.login,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                final String mTAN = ((TextView) loginView.findViewById(R.id.mtan)).getText().toString().trim();
                                final MTan2FACommand command = new MTan2FACommand(AuthenticationManager.this.configuration);
                                command.execute(requestId, mTAN, new CommandCallback() {
                                    @Override
                                    public void onSuccess(Object data) {
                                        authentication = new BiproSAMLAuthentication((String)data);
                                        successCallback.run(); // und endlich zurück
                                    }
                                    @Override
                                    public void onFailure(Exception e) {
                                        authentication = null;
                                        ContextCompat.getMainExecutor(context).execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                builder.setTitle("Fehler bei Authentifizierung");
                                                builder.setMessage(e.getMessage());
                                                builder.setIcon(R.drawable.ic_error_black_24dp);
                                                builder.setNeutralButton(R.string.cancel, (dialog, which) -> {});
                                                builder.create().show();
                                            }
                                        });
                                    }
                                });

                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        alertDialogBuilder.show();
    }

    public BiproAuthentication getAuthentication() {
        return authentication;
    }
}
