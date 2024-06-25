package com.iherb.herb.utils;

import ai.djl.Device;
import ai.djl.Model;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.transform.Normalize;
import ai.djl.modality.cv.transform.RandomResizedCrop;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.modality.cv.translator.ImageClassificationTranslator;
import ai.djl.translate.Translator;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class HerbRecUtil {

    private static final int INPUT_SIZE = 224;

    private List<String> herbNames;

    Predictor<Image, Classifications> predictor;

    private Model model;

    public HerbRecUtil() {
        this.loadHerbNames();
        this.init();
    }

    private void init() {
        Translator<Image, Classifications> translator = ImageClassificationTranslator.builder()
                .addTransform(new RandomResizedCrop(INPUT_SIZE, INPUT_SIZE, 0.6, 1,
                        3. / 4, 4. / 3))
                .addTransform(new ToTensor())
                .addTransform(new Normalize(
                        new float[] {0.5f, 0.5f, 0.5f},
                        new float[] {0.5f, 0.5f, 0.5f}))
                .optApplySoftmax(true)
                .optSynset(herbNames)
                .optTopK(5)
                .build();
        Model model = Model.newInstance("model", Device.cpu());
        try {
            InputStream inputStream = HerbRecUtil.class.getClassLoader().getResourceAsStream("script1.pt");
            if (inputStream == null) {
                throw new RuntimeException("找不到模型文件");
            }
            model.load(inputStream);

            predictor = model.newPredictor(translator);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Classifications.Classification> predict(InputStream inputStream) {
        List<Classifications.Classification> result = new ArrayList<>();
        Image input = this.resizeImage(inputStream);
        try {
            Classifications output = predictor.predict(input);
            System.out.println(output);
            result = output.topK();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void loadHerbNames() {
        BufferedReader reader = null;
        herbNames = new ArrayList<>();
        try {
            InputStream in = HerbRecUtil.class.getClassLoader().getResourceAsStream("names.txt");
            reader = new BufferedReader(new InputStreamReader(in));
            String name = null;
            while ((name = reader.readLine()) != null) {
                herbNames.add(name);
            }
            System.out.println(herbNames);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Image resizeImage(InputStream inputStream) {
        BufferedImage input = null;
        try {
            input = ImageIO.read(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int iw = input.getWidth(), ih = input.getHeight();
        int w = INPUT_SIZE, h = INPUT_SIZE;
        double scale = Math.min(1. *  w / iw, 1. * h / ih);
        int nw = (int) (iw * scale), nh = (int) (ih * scale);
        java.awt.Image img;
        boolean needResize = 1. * iw / ih > 1.4 || 1. * ih / iw > 1.4;
        if (needResize) {
            img = input.getScaledInstance(nw, nh, BufferedImage.SCALE_SMOOTH);
        } else {
            img = input.getScaledInstance(INPUT_SIZE, INPUT_SIZE, BufferedImage.SCALE_SMOOTH);
        }
        BufferedImage out = new BufferedImage(INPUT_SIZE, INPUT_SIZE, BufferedImage.TYPE_INT_RGB);
        Graphics g = out.getGraphics();
        g.setColor(new Color(128, 128, 128));
        g.fillRect(0, 0, INPUT_SIZE, INPUT_SIZE);
        out.getGraphics().drawImage(img, 0, needResize ? (INPUT_SIZE - nh) / 2 : 0, null);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(outputStream);
            ImageIO.write(out, "jpg", imageOutputStream);
            InputStream is = new ByteArrayInputStream(outputStream.toByteArray());
            return ImageFactory.getInstance().fromInputStream(is);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("图片转换失败");
        }
    }
}
