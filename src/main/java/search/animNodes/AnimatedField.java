package search.animNodes;

import javafx.animation.*;
import javafx.scene.control.*;
import javafx.util.*;

public class AnimatedField extends TextField {
    private final double maxWidth, width;

    public AnimatedField(double width, double height, String pText) {
        setPromptText(pText);
        setWidth(width);
        setHeight(height);
        setPrefWidth(width);
        setPrefHeight(height);
        maxWidth = width + 15;
        this.width = width;

        setAnimation();
    }

    private void setAnimation() {
        Timeline timeline = new Timeline();

        setOnMouseEntered(e -> {
            timeline.stop();

            KeyValue keyValue = new KeyValue(prefWidthProperty(), maxWidth, Interpolator.SPLINE(0, 0, 0, 1));
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), keyValue);
            timeline.getKeyFrames().clear();
            timeline.getKeyFrames().add(keyFrame);
            timeline.play();
        });
        setOnMouseExited(e -> {
            timeline.stop();

            KeyValue keyValue = new KeyValue(prefWidthProperty(), width, Interpolator.SPLINE(0, 0, 0, 1));
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), keyValue);
            timeline.getKeyFrames().clear();
            timeline.getKeyFrames().add(keyFrame);
            timeline.play();
        });
    }
}
