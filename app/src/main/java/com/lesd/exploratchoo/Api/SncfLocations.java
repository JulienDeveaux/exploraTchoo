package com.lesd.exploratchoo.Api;

import java.util.Locale;

public enum SncfLocations
{
    LE_HAVRE("SNCF:87413013");

    private final String displayName;
    private final String id;

    SncfLocations(String id)
    {
        this.displayName = this.name().replace("_", " ").toLowerCase(Locale.ROOT);
        this.id = id;
    }

    public String getId()
    {
        return id;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    @Override
    public String toString()
    {
        return this.id;
    }
}
