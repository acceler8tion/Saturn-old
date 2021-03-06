package com.acceler8tion.Saturn.entity.command.server;

import com.acceler8tion.Saturn.database.DataBase;
import com.acceler8tion.Saturn.util.EmojiFactory;
import com.acceler8tion.Saturn.entity.annotation.command.CommandDoc;
import com.acceler8tion.Saturn.entity.annotation.command.CommandProperties;
import com.acceler8tion.Saturn.entity.command.IGuildCommand;
import com.acceler8tion.Saturn.event.IGuildMessageReceivedEvent;
import com.acceler8tion.Saturn.util.PermissionLevel;
import com.acceler8tion.Saturn.util.Translator;

@CommandProperties(level = PermissionLevel.ADMIN)
@CommandDoc(
        alias = "saturn.language",
        preview = "사용 언어 변경"
)
public class ChangeServerLanguage extends IGuildCommand {

    @Override
    public void run(IGuildMessageReceivedEvent event) {
        super.run(event);

        String[] splitted = event.getSplitted();
        if(splitted.length == 2){
            Translator.Language lang = Translator.Language.keyToLang(splitted[1]);
            if(lang == null){
                Translator.doOnTranslate(event.getConfiguration().getLang(), "saturn_language_no_lang", event, (s, ievent) -> {
                    ievent.getTextChannel().sendMessage(String.format(s, splitted[1], EmojiFactory.CONFUSED_FACE.getEmoji())).queue(m -> m.addReaction(EmojiFactory.WHITE_CHECK_MARK.getEmoji()).queue());
                });
            } else {
                DataBase.serverConfigurationDB.update(db -> {
                    db.getJSONObject(event.getConfiguration().getId()).put("language", lang.getLang());

                    return db;
                });
                Translator.doOnTranslate(lang, "saturn_language_changed", event, (s, ievent) -> {
                    ievent.getTextChannel().sendMessage(String.format(s, splitted[1], EmojiFactory.GRINNING_FACE.getEmoji())).queue(m -> m.addReaction(EmojiFactory.WHITE_CHECK_MARK.getEmoji()).queue());
                });
            }
        } else {
            Translator.doOnTranslate(event.getConfiguration().getLang(), "saturn_language_empty_lang", event, (s, ievent) -> {
                ievent.getTextChannel().sendMessage(String.format(s, EmojiFactory.ANGRY_FACE.getEmoji(), String.join(",", Translator.keySet().toArray(new String[]{})))).queue(m -> m.addReaction(EmojiFactory.WHITE_CHECK_MARK.getEmoji()).queue());
            });
        }
    }

}
