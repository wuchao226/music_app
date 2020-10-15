package com.wuc.ft_audio.mediaplayer.events;


import com.wuc.ft_audio.mediaplayer.core.CustomMediaPlayer;

public class AudioProgressEvent {

  public CustomMediaPlayer.Status mStatus;
  public int progress;
  public int maxLength;

  public AudioProgressEvent(CustomMediaPlayer.Status status, int progress, int maxLength) {
    this.mStatus = status;
    this.progress = progress;
    this.maxLength = maxLength;
  }
}