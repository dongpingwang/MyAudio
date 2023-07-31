---
title: AudioFocus申请失败情形
date: 2023-07-29T15:04:37Z
lastmod: 2023-07-31T17:18:09Z
---

# AudioFocus申请失败情形

最近在分析问题时，经常看到媒体应用出现申请音频焦点失败，导致状态异常的问题，因此总结一下出现该问题的一些情景。原生设计中，**通话中或者来电时，**申请音频焦点失败，需要关注下。

* 缺少权限
* 音频焦点栈满了
* IPC状态异常
* **通话中或者来电时**
* 申请焦点时带有AUDIOFOCUS_FLAG_LOCK标志

```java

// -----------------------------MediaControlFocus.java-----------------------------
public int requestAudioFocus() {
    ...
    boolean focusGrantDelayed = false;
    if (!canReassignAudioFocus()) {
        if ((flags & AudioManager.AUDIOFOCUS_FLAG_DELAY_OK) == 0) {
            return AudioManager.AUDIOFOCUS_REQUEST_FAILED;
        } else {
            // request has AUDIOFOCUS_FLAG_DELAY_OK: focus can't be
            // granted right now, so the requester will be inserted in the focus stack
            // to receive focus later
            focusGrantDelayed = true;
        }
    }
    ...
}

/**
 * Helper function:
 * Returns true if the system is in a state where the focus can be reevaluated, false otherwise.
 * The implementation guarantees that a state where focus cannot be immediately reassigned
 * implies that an "locked" focus owner is at the top of the focus stack.
 * Modifications to the implementation that break this assumption will cause focus requests to
 * misbehave when honoring the AudioManager.AUDIOFOCUS_FLAG_DELAY_OK flag.
 */
private boolean canReassignAudioFocus() {
    // focus requests are rejected during a phone call or when the phone is ringing
    // this is equivalent to IN_VOICE_COMM_FOCUS_ID having the focus
    if (!mFocusStack.isEmpty() && isLockedFocusOwner(mFocusStack.peek())) {
        return false;
    }

    // 返回false的情景：通话中或者来电时；申请焦点时带有AUDIOFOCUS_FLAG_LOCK标志
    return true;
}

private boolean isLockedFocusOwner(FocusRequester fr) {
    return (fr.hasSameClient(AudioSystem.IN_VOICE_COMM_FOCUS_ID) || fr.isLockedFocusOwner());
}

// -----------------------------AudioSystem.java-----------------------------
/**
 * Constant to identify a focus stack entry that is used to hold the focus while the phone
 * is ringing or during a call. Used by com.android.internal.telephony.CallManager when
 * entering and exiting calls.
 */
public final static String IN_VOICE_COMM_FOCUS_ID = "AudioFocus_For_Phone_Ring_And_Calls";

// -----------------------------FocusRequester.java-----------------------------
boolean isLockedFocusOwner() {
    return ((mGrantFlags & AudioManager.AUDIOFOCUS_FLAG_LOCK) != 0);
}
```

‍
