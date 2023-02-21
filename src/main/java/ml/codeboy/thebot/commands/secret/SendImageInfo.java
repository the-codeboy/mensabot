package ml.codeboy.thebot.commands.secret;

import ml.codeboy.thebot.MensaUtil;
import ml.codeboy.thebot.data.FoodRatingManager;
import ml.codeboy.thebot.data.UserData;
import ml.codeboy.thebot.data.UserDataManager;
import ml.codeboy.thebot.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.PrivateChannel;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

public class SendImageInfo extends SecretCommand {
    public SendImageInfo() {
        super("sendInfo", "", "info");
    }

    @Override
    public void run(CommandEvent event) {
        Collection<MealImageWrapper> images = FoodRatingManager.getInstance().getImages();
        for (UserData user : UserDataManager.getInstance().getAllUserData()) {
            ArrayList<MealImageWrapper> imgs = new ArrayList<>();
            for (MealImageWrapper i : images) {
                if (i.getAuthor().equals(user.getId())&&i.getUrl().contains("ephemeral"))
                    imgs.add(i);
            }
            if (!imgs.isEmpty()) {
                PrivateChannel channel = user.getUser(event.getJDA()).openPrivateChannel().complete();
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.RED).setTitle("Essensbilder weg :(")
                        .setDescription("Vielen Dank, dass du Bilder beim Mensabot hochlädst. Leider sind die folgenden Bilder nicht mehr vorhanden:");
                for (MealImageWrapper image : imgs) {
                    builder.addField(MensaUtil.getEmojiForWord(image.getMeal()) + " " + image.getMeal(), "", false);
                }
                builder.setFooter("Es wäre super, wenn du mir die Bilder schicken könntest, falls du sie noch hast. Einfach hier als Nachricht reicht.");
                channel.sendMessageEmbeds(builder.build()).queue();
            }
        }
    }
}
