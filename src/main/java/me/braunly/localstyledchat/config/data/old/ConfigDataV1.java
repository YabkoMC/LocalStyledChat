package me.braunly.localstyledchat.config.data.old;

import eu.pb4.placeholders.TextParser;
import me.braunly.localstyledchat.config.data.ChatStyleData;
import me.braunly.localstyledchat.config.data.ConfigData;

import java.util.HashMap;
import java.util.stream.Collectors;

public class ConfigDataV1 {
    public int CONFIG_VERSION_DONT_TOUCH_THIS = 1;
    public String _comment = "Before changing anything, see https://github.com/Patbox/StyledChat#configuration";
    public ChatStyleData defaultStyle = ChatStyleData.DEFAULT;
    public HashMap<String, ChatStyleData> permissionStyles = new HashMap<>();
    public boolean legacyChatFormatting = false;
    public HashMap<String, Boolean> defaultEnabledFormatting = getDefaultFormatting();


    private static HashMap<String, Boolean> getDefaultFormatting() {
        HashMap<String, Boolean> map = new HashMap<>();
        for (String string : TextParser.getRegisteredTags().keySet()) {
            map.put(string, false);
        }
        map.put("item", true);
        return map;
    }

    public ConfigData updateToV2() {
        ConfigData data = new ConfigData();
        data.defaultStyle = this.defaultStyle;
        data.legacyChatFormatting = this.legacyChatFormatting;
        data.defaultEnabledFormatting = this.defaultEnabledFormatting;

        data.permissionStyles = this.permissionStyles.entrySet().stream().map((entry) -> ConfigData.PermissionPriorityStyle.of(entry.getKey(), entry.getValue())).collect(Collectors.toList());

        return data;
    }
}
