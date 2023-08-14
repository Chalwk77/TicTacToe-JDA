/* Copyright (c) 2023, TicTacToe. Jericho Crosby <jericho.crosby227@gmail.com> */

package com.chalwk.commands;

import com.chalwk.listeners.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.chalwk.game.Game.*;

public class Invite implements CommandInterface {

    @Override
    public String getName() {
        return "invite";
    }

    @Override
    public String getDescription() {
        return "Invite someone to play TicTacToe with you.";
    }

    @Override
    public List<OptionData> getOptions() {

        List<OptionData> options = new ArrayList<>();
        OptionData user = new OptionData(OptionType.USER, "opponent", "The user you want to invite.");
        user.setRequired(true);

        OptionData board = new OptionData(OptionType.INTEGER, "board", "The board size you want to play on.");

        for (int i = 0; i < boards.length; i++) {
            String size = boards[i].length + "x" + boards[i].length;
            board.addChoice(size, i);
        }
        board.setRequired(true);

        options.add(user);
        options.add(board);

        return options;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {

        Guild guild = event.getGuild();
        String guildID = guild.getId();

        OptionMapping option = event.getOption("opponent");
        OptionMapping size = event.getOption("board");

        if (option.getAsUser().isBot()) {
            event.reply("You can't invite a bot to play Tic-Tac-Toe.").setEphemeral(true).queue();
        } else {

            String inviteeID = event.getUser().getId();
            String opponentID = option.getAsUser().getId();

            newBoard(size);
            showSubmission(event, inviteeID, opponentID);
            games.put(guildID, new String[]{inviteeID, opponentID, "false"});

//            Timer timer = new Timer();
//            timer.schedule(new java.util.TimerTask() {
//                @Override
//                public void run() {
//                    games.remove(guildID);
//                    event.getHook().deleteOriginal().queue();
//                }
//            }, 1000*60);
        }
    }

    private void showSubmission(SlashCommandInteractionEvent event, String inviteeID, String opponentID) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("⭕.❌ Tic-Tac-Toe ❌.⭕");
        embed.setDescription("<@" + opponentID + "> You have been invited to play TicTacToe by <@" + inviteeID + ">.");
        embed.addField("Board Size:", board.length + "x" + board.length, true);
        embed.addField("\nA random player will be selected to go first.", "", false);
        embed.setFooter("Submission will expire in 60 seconds.");

        List<Button> buttons = new ArrayList<>();
        buttons.add(Button.success("accept", "\uD83D\uDFE2 Accept"));
        buttons.add(Button.danger("decline", "\uD83D\uDD34 Decline"));

        event.replyEmbeds(embed.build()).addActionRow(buttons).queue();
    }

    private void newBoard(OptionMapping boardSize) {
        board = boards[boardSize.getAsInt()];
        letters = Arrays.copyOfRange(new String[]{
                "A", "B", "C", "D",
                "E", "F", "G", "H",
                "I", "J", "K", "L",
                "M", "N", "O", "P",
                "Q", "R", "S", "T",
                "U", "V", "W", "X",
                "Y", "Z"
        }, 0, board.length);
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board.length; col++) {
                board[row][col] = filler;
                cell_indicators.put(letters[row] + (col + 1), new int[]{col, row});
            }
        }
    }
}
