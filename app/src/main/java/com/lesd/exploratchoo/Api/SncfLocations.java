package com.lesd.exploratchoo.Api;

import java.util.Locale;

public enum SncfLocations
{
    LE_HAVRE("SNCF:87413013"),
    ROUEN("SNCF:87401893"),
    PARIS_SAINT_LAZARE("SNCF:87384008"),
    PARIS_GARE_DU_NORD("SNCF:87271007"),
    YVETOT("SNCF:87413385"),
    BREAUTE_BAUZEVILLE("SNCF:87413344");

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
