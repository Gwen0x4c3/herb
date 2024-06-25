package com.iherb.herb.utils;

import ai.djl.Device;
import ai.djl.Model;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.transform.Normalize;
import ai.djl.modality.cv.transform.Resize;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.translate.Transform;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;

import java.io.IOException;
import java.io.InputStream;

public class FeatureExtractUtils {

    private static final int IMAGE_SIZE = 224;
    private static Model model;
    private static Predictor<Image, float[]> predictor;
    static {
        try {
            model = Model.newInstance("model");
            model.load(FeatureExtractUtils.class.getClassLoader().getResourceAsStream("resnet.pt"));
            Transform resize = new Resize(IMAGE_SIZE);
            Transform toTensor = new ToTensor();
            Transform normalize = new Normalize(new float[]{0.485f, 0.456f, 0.406f}, new float[]{0.229f, 0.224f, 0.225f});
            Translator<Image, float[]> translator = new Translator<Image, float[]>() {
                @Override
                public NDList processInput(TranslatorContext ctx, Image input) throws Exception {
                    NDManager ndManager = ctx.getNDManager();
                    NDArray transform = normalize.transform(toTensor.transform(resize.transform(input.toNDArray(ndManager))));
                    NDList list = new NDList();
                    list.add(transform);
                    return list;
                }
                @Override
                public float[] processOutput(TranslatorContext ctx, NDList ndList) throws Exception {
                    return ndList.get(0).toFloatArray();
                }
            };
            predictor = new Predictor<>(model, translator, Device.cpu(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static float[] getFeature(InputStream inputStream) {
        try {
            float[] vector = predictor.predict(ImageFactory.getInstance().fromInputStream(inputStream));
            return vector;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
