package com.m7.imkfsdk.view.imageviewer.subscaleview.decoder;


import androidx.annotation.NonNull;

import java.lang.reflect.InvocationTargetException;

/**
 * Interface for {@link IMoorImageDecoder} and {@link IMoorImageRegionDecoder} factories.
 * @param <T> the class of decoder that will be produced.
 */
public interface IMoorDecoderFactory<T> {

    /**
     * Produce a new instance of a decoder with type {@link T}.
     * @return a new instance of your decoder.
     * @throws IllegalAccessException if the factory class cannot be instantiated.
     * @throws InstantiationException if the factory class cannot be instantiated.
     * @throws NoSuchMethodException if the factory class cannot be instantiated.
     * @throws InvocationTargetException if the factory class cannot be instantiated.
     */
    @NonNull
    T make() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException;

}
