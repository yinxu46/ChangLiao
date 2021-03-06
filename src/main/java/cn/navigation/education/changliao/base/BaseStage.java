package cn.navigation.education.changliao.base;

import cn.navigation.education.changliao.tool.AssetLoader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 所有窗口的基类
 */

public abstract class BaseStage extends Stage {
    public Scene scene;
    private boolean isMaxinize = false;
//    private double oldStageX;
//
//    private double oldStageY;
//
//    private double oldScreenX;
//
//    private double oldScreenY;

    public final static Map<String, Stage> STAGE_CONTEXT = new WeakHashMap<>();

    public BaseStage(String fxml) {
        STAGE_CONTEXT.put(this.getClass().getName(), this);
        Parent root = null;
        try {
            root = FXMLLoader.load(ClassLoader.getSystemResource(fxml));
        } catch (IOException e) {
            e.printStackTrace();
        }
        scene = new Scene(root);
        setScene(scene);
        setTitle("畅聊");
        initStyle(StageStyle.UNDECORATED);

        getIcons().add(AssetLoader.loadAssetImage("images/icon.png", 200, 200));
        setWidth(900);
        setHeight(600);
        show();
//        setWindowDrag();
    }

    /**
     * 调整窗体大小及位置
     */
    public void setWindowSize() {
        if (!isMaxinize) {
            Screen screen = Screen.getPrimary();
            setHeight(screen.getVisualBounds().getHeight());
            setWidth(screen.getVisualBounds().getWidth());
        } else {
            setHeight(600);
            setWidth(900);
        }
        centerOnScreen();
        isMaxinize = !isMaxinize;
    }

//    /**
//     * 设置窗体拖动
//     */
//    private void setWindowDrag() {
//        EventHandler handler = this;
//        scene.setOnMousePressed(handler);
//        scene.setOnMouseDragOver(handler);
//    }
//
//    @Override
//    public void handle(MouseEvent e) {
//        System.out.println(e.getY());
//        if (e.getEventType() == MouseEvent.MOUSE_PRESSED) {    //鼠标按下的事件
//            this.oldStageX = this.getX();
//            this.oldStageY = this.getY();
//            this.oldScreenX = e.getScreenX();
//            this.oldScreenY = e.getScreenY();
//        } else if (e.getEventType() == MouseEvent.MOUSE_MOVED) {
//            this.setX(e.getScreenX() - this.oldScreenX + this.oldStageX);
//            this.setY(e.getScreenY() - this.oldScreenY + this.oldStageY);
//        }
//
//    }
}
