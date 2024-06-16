package ml.codeboy.thebot.commands.leaderboard;

import kotlin.jvm.functions.Function2;
import ml.codeboy.thebot.data.UserData;
import ml.codeboy.thebot.listeners.CommandHandler;

import java.util.function.Function;

public class LeaderBoard {
    private static final LeaderBoard karma = new LeaderBoard("Karma", "Karma", UserData::getKarma, ((data, integer) -> {
        data.setKarma(integer);
        return null;
    })),
            sus = new LeaderBoard("Sus", "Suscount", UserData::getSusCount, ((data, integer) -> {
                data.setSusCount(integer);
                return null;
            }));
    private final Function<UserData, Integer> getValue;
    private final Function2<UserData, Integer, Void> setValue;
    private final String name, currency;
    private boolean hasTop = true, hasBottom = true;

    public LeaderBoard(String name, String currency, Function<UserData, Integer> getValue, Function2<UserData, Integer, Void> setValue) {
        this.getValue = getValue;
        this.setValue = setValue;
        this.name = name;
        this.currency = currency;
    }

    public static void registerAll(CommandHandler handler) {
        karma.register(handler);
        sus.register(handler);
    }

    public void register(CommandHandler handler) {
        handler.registerCommand(new LeaderBoardCommand(this));
        if (hasTop)
            handler.registerCommand(new TopCommand(this));
        if (hasBottom)
            handler.registerCommand(new BottomCommand(this));
    }

    public void setHasTop(boolean hasTop) {
        this.hasTop = hasTop;
    }

    public void setHasBottom(boolean hasBottom) {
        this.hasBottom = hasBottom;
    }

    public String getName() {
        return name;
    }

    public String getCurrency() {
        return currency;
    }

    public int getValue(UserData data) {
        return getValue.apply(data);
    }

    public void setSetValue(UserData data, int value) {
        setValue.invoke(data, value);
    }
}
