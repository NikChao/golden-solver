package org.goldenpath.solver.utils;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;

public class AppIcon {
    public void setAppIcon(Image icon, Stage primaryStage) {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("mac")) {
            try {
                // Use reflection to avoid compile-time dependency on com.apple.eawt
                Class<?> appClass = Class.forName("com.apple.eawt.Application");
                Object macApp = appClass.getDeclaredMethod("getApplication").invoke(null);
                appClass.getDeclaredMethod("setDockIconImage", java.awt.Image.class)
                        .invoke(macApp, convertToBufferedImage(icon));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        primaryStage.getIcons().add(icon);
    }

    private static BufferedImage convertToBufferedImage(Image fxImage) {
        int width = (int) fxImage.getWidth();
        int height = (int) fxImage.getHeight();
        BufferedImage bImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        PixelReader pixelReader = fxImage.getPixelReader();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color fxColor = pixelReader.getColor(x, y);
                int argb = ((int) (fxColor.getOpacity() * 255) << 24) |
                        ((int) (fxColor.getRed() * 255) << 16) |
                        ((int) (fxColor.getGreen() * 255) << 8) |
                        ((int) (fxColor.getBlue() * 255));
                bImage.setRGB(x, y, argb);
            }
        }
        return bImage;
    }
}
