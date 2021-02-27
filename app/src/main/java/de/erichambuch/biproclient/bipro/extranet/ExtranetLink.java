package de.erichambuch.biproclient.bipro.extranet;

import org.jetbrains.annotations.NotNull;

public class ExtranetLink {

    public final String title;
    public final String url;

    public ExtranetLink(String title, String url) {
        this.title = title;
        this.url = url;
    }

    @NotNull
    public String toString() {
        return title+": "+url;
    }
}
