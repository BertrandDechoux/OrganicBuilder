package uk.org.squirm3.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

public class Utils {
	public static final Color BACKGROUND = Color.rgb(255, 255, 225);
	
	public static void defaultBackground(Region region) {
		region.setBackground(new Background(new BackgroundFill(//
				Utils.BACKGROUND, //
				CornerRadii.EMPTY, //
				Insets.EMPTY)));

	}

	public static void defaultBorder(Pane pane) {
		pane.setBorder(new Border(new BorderStroke(//
				Color.GREY, //
				BorderStrokeStyle.SOLID, //
				CornerRadii.EMPTY, //
				BorderWidths.DEFAULT)));
	}
	
	public static void defaultSize(Button button) {
        button.setMinWidth(80);
        button.setMaxWidth(80);
        button.setMinHeight(25);
        button.setMaxHeight(25);
	}
}
