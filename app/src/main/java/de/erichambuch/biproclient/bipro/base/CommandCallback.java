package de.erichambuch.biproclient.bipro.base;

/**
 * Callback für Ausführungsergebnis eines {@link BiproServiceCommand}.
 */
public interface CommandCallback {
    public void onSuccess(Object data);
    public void onFailure(Exception e);
}
