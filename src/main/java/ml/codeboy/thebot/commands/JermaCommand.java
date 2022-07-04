package ml.codeboy.thebot.commands;

import ml.codeboy.thebot.events.CommandEvent;

import java.util.Random;

public class JermaCommand extends Command {

    private static final String[] imgs = {
            "https://static.wikia.nocookie.net/jerma-lore/images/e/e3/JermaSus.jpg/revision/latest?cb=20201206225609",
            "https://static.wikia.nocookie.net/jerma-lore/images/9/91/Evil_Jerma.png/revision/latest?cb=20180210022646",
            "https://static.wikia.nocookie.net/youtube/images/d/da/Jerma985-2018.jpg/revision/latest?cb=20210112042437",
            "https://preview.redd.it/zhol0457vjo71.jpg?width=640&crop=smart&auto=webp&s=4f9d6fdfd179e343eceb00bf67e95dbc62dc4371",
            "https://m.media-amazon.com/images/I/31VPBUdCm7L._AC_SY450_.jpg",
            "https://themarketactivity.com/wp-content/uploads/2021/07/4pu4hh3jua651.jpg",
            "https://c.tenor.com/mbBqkx519GMAAAAC/jerma985-jerma.gif",
            "https://preview.redd.it/954sqhne4zo61.jpg?auto=webp&s=a417f14db2e477d3344c998fb8b2d63eda4d2f44",
            "https://i1.sndcdn.com/artworks-vFNnqiI5ivzCeCp7-cLGSAA-t500x500.jpg",
            "https://static.wikia.nocookie.net/jerma-lore/images/6/6c/TheThing.jpg/revision/latest?cb=20220226113141",
            "https://preview.redd.it/3bs3k6wz2e351.jpg?auto=webp&s=7c3420e62d0236774ebe9f6977f8218b76afa7ed",
            "https://lastfm.freetls.fastly.net/i/u/ar0/db66c59a7eac5e52c30ef3c0d5748f28",
            "https://preview.redd.it/kb9j3vfxuje61.jpg?width=640&crop=smart&auto=webp&s=b0d2a7682c8e5931b0c8e6a6142fb0efd7b47d9e",
            "https://preview.redd.it/p2d7k9cs5fh31.jpg?auto=webp&s=872e1b73af9613b7fcc8144f12e43fb6aeefe433",
            "https://i1.sndcdn.com/artworks-gFHQfeYpaCfbMYio-cf5D2A-t240x240.jpg",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRSHCUyJZMbSCxte6GCdTFxtLBBoQsNCIRe6A&usqp=CAU",
            "https://i1.sndcdn.com/artworks-lxDqHP6qHdNrhj24-y4l7ag-t240x240.jpg",
            "https://img.wattpad.com/cover/192991135-256-k278173.jpg",
            "https://64.media.tumblr.com/472bd9a075535cc689e76c73bf18d555/0edff908cb6a2d13-0a/s250x400/91e055646ac1581ce4d1dc316454193fa4525032.png",
            "https://64.media.tumblr.com/d65ecb339455ef61ef1629fbd7f87eba/dd50bbf401aef9ec-c7/s500x750/097e4d8538f30d2c15ea0600b16b6d62c8cd2691.jpg",
            "https://78.media.tumblr.com/6f068d4ce745609347de4e77ea3214ec/dd50bbf401aef9ec-10/s640x960/8f499e2b4b30d26ec958763701077496f0cb9d17.jpg",
            "https://i.redd.it/zp755s2j4g281.jpg",
            "https://img.wattpad.com/cover/192991135-256-k278173.jpg",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT2e8Us8bGMKnc7dPKQieKTM8w6FeoWuYRzUg&usqp=CAU",
            "https://ih1.redbubble.net/image.2755485180.2949/fposter,small,wall_texture,product,750x1000.jpg"
    };
    private Random rand;

    public JermaCommand() {
        super("jerma", "Sends a Jerma");
        rand = new Random();
        rand.setSeed(System.currentTimeMillis());
    }

    @Override
    public void run(CommandEvent event) {
        event.reply(imgs[rand.nextInt(imgs.length)]);
    }
}
