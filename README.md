Music_Info_on_Lockscreen
========================

Music Information on lockscreen (Controls, Trackname, AlbumArt)

MusicHelper must be put on SystemUI, it catch intents because catching it directly in lockscreen will cause reboot/force close.
The catched intents will be put on ContentResolver, and its been observed on the controls, trackname, albumart (Thanks to PineappleOwl for teaching me how to use ContentResolver)

Credits:
CyanogenMod,
 PineappleOwl,
 AOSP,
 Spacecaker,
 Lenox Devs,
 Potato Inc.
