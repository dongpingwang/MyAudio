package android.media.audiopolicy;
import android.content.Context;
import android.media.AudioFocusInfo;

public class AudioPolicy {
    public static class Builder {
        private Context mContext;
        private AudioPolicyFocusListener mFocusListener;
        public Builder(Context context) {}
        public void setAudioPolicyFocusListener(AudioPolicyFocusListener l) {}
        public Builder setIsAudioFocusPolicy(boolean isFocusPolicy) {
            return this;
        }
        public AudioPolicy build() {
            return new AudioPolicy();
        }
    }

    public static abstract class AudioPolicyFocusListener {
        public void onAudioFocusGrant(AudioFocusInfo afi, int requestResult) {}
        public void onAudioFocusLoss(AudioFocusInfo afi, boolean wasNotified) {}
        public void onAudioFocusRequest(AudioFocusInfo afi, int requestResult) {}
        public void onAudioFocusAbandon(AudioFocusInfo afi) {}
    }
}
