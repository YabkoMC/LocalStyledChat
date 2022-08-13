package me.braunly.localstyledchat.ducks;

import net.minecraft.text.Text;

public interface ExtSignedMessage {
    //void styledChat_setOriginal(String message);
    void styledChat_setArg(String name, Text arg);
    String styledChat_getOriginal();
    Text styledChat_getArg(String name);
}
