package me.hydos.trinityutils.model.animation;

import com.google.gson.annotations.SerializedName;
import me.hydos.trinityutils.model.animation.track.AnimationTrack;

import java.util.List;

public class TrinityAnimation {
    public Info info;
    @SerializedName("track")
    public Tracks tracks;

    public TrinityAnimation() {
    }

    public static class Info {
        @SerializedName("does_loop")
        public int doesLoop;
        @SerializedName("animation_count")
        public int frameCount; // amount of frames + 1. Dynamic channels use this but - 1
        @SerializedName("animation_rate")
        public int tickRate;
    }

    public static class Tracks {
        public List<BoneTrack> tracks;
    }

    public static class BoneTrack {
        @SerializedName("bone_name")
        public String boneName;
        @SerializedName("scale_type")
        public String scaleType;
        public AnimationTrack scale;

    }
}
