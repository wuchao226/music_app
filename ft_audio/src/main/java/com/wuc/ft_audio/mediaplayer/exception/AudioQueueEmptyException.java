package com.wuc.ft_audio.mediaplayer.exception;

/**
 * @author: wuchao
 * @date: 2019-10-09 16:45
 * @desciption: 播放队列为空异常
 */
public class AudioQueueEmptyException extends RuntimeException {

    public AudioQueueEmptyException(String error) {
        super(error);
    }
}
