package com.the_codeboy.mensabot.apis;

import net.dv8tion.jda.api.EmbedBuilder;

public class TrumpQuote {
    private String value;

    public EmbedBuilder createEmbed(EmbedBuilder builder) {
        builder.setDescription(value);
        builder.setAuthor("Donald Trump", null, "https://www.tronalddump.io/img/tronalddump_850x850.png");
        return builder;
    }
}
