package org.rhm.item_cooldown_api.api;

public class Cooldown {
    private Cooldown(Builder builder) {

    }

    public static class Builder {

        public Cooldown build() {
            return new Cooldown(this);
        }
    }
}