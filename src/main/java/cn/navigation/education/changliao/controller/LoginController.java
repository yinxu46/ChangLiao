package cn.navigation.education.changliao.controller;

import cn.navigation.education.changliao.base.BaseController;
import cn.navigation.education.changliao.handler.TcpHandler;
import cn.navigation.education.changliao.pages.Login;
import cn.navigation.education.changliao.pages.MainPage;
import cn.navigation.education.changliao.tool.AssetLoader;
import cn.navigation.education.changliao.utils.StringUtils;
import com.jfoenix.controls.JFXButton;
import io.vertx.core.json.JsonObject;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Pagination;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.WindowEvent;
import org.controlsfx.control.Notifications;

import java.net.URL;
import java.util.ResourceBundle;

import static cn.navigation.education.changliao.base.BaseStage.STAGE_CONTEXT;
import static cn.navigation.education.changliao.config.Constant.*;
import static cn.navigation.education.changliao.MainVerticle.vertx;

public class LoginController extends BaseController implements Initializable {
    @FXML
    private HBox topBox;
    @FXML
    private HBox titleBox;
    @FXML
    private HBox topControllerBar;
    @FXML
    private Pagination pagination;

    private JFXButton close;
    private JFXButton minimize;

    private BorderPane QRCodeLoginPane;
    private BorderPane accountLogin;
    private Hyperlink switchLogin;
    private JFXButton loginAction;
    private TextField userName;
    private PasswordField password;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initView();
        event();
    }

    public void initView() {
        titleBox.prefWidthProperty().bind(topBox.widthProperty().multiply(0.5));
        topControllerBar.prefWidthProperty().bind(topBox.widthProperty().multiply(0.5));
        //移除最大化图标
        topControllerBar.getChildren().remove(1);
        close = (JFXButton) topControllerBar.lookup("#close");
        minimize = (JFXButton) topControllerBar.lookup("#minimize");
        QRCodeLoginPane = (BorderPane) AssetLoader.loadLayout("fxml/items/qr_code_login.fxml").lookup("#container");
        accountLogin = (BorderPane) AssetLoader.loadLayout("fxml/items/account_login.fxml").lookup("#container");
        password = (PasswordField) accountLogin.lookup("#password");
        userName = (TextField) accountLogin.lookup("#userName");
        loginAction = (JFXButton) accountLogin.lookup("#loginAction");

        switchLogin = (Hyperlink) QRCodeLoginPane.lookup("#switchLogin");
        pagination.setPageFactory(this::pageFactory);
    }

    private BorderPane pageFactory(int index) {
        if (index == 0) {
            return QRCodeLoginPane;
        } else {
            return accountLogin;
        }
    }

    private void event() {
        close.setOnAction(e -> {
            Platform.exit();
            System.exit(1);
        });
        minimize.setOnAction(e -> {
            var stage = STAGE_CONTEXT.get(Login.class.getName());
            stage.setIconified(true);
        });
        /**
         * 切换到账号登陆
         */
        switchLogin.setOnAction(e -> pagination.setCurrentPageIndex(1));
        loginAction.setOnAction(this::login);

    }

    private void login(ActionEvent e) {
        if (userName.getText().trim().equals("")) {
            Notifications.create().position(Pos.TOP_CENTER).text("用户名不能为空!").showWarning();
            return;
        }
        if (password.getText().trim().equals("")) {
            Notifications.create().position(Pos.TOP_CENTER).text("密码不能为空!").showWarning();
            return;
        }
        var data = new JsonObject();
        data.put(TYPE, USER);
        data.put(SUBTYPE, LOGIN);
        data.put(PASSWORD, StringUtils.toMd5(password.getText()));
        data.put(ID, userName.getText());
        vertx.eventBus().send(TcpHandler.class.getName(), data,ar->{
            if (!ar.succeeded()){
                System.out.println("连接服务器失败:" + ar.cause());
                return;
            }
            System.out.println(ar.result().body());
        });
    }


    @Override
    public void updateUi(JsonObject data) {
        var login = data.getBoolean(LOGIN);
        if (login) {
            //储存当前账号信息
            CURRENT_ACCOUNT.put(ID, data.getString(ID) == null ? data.getString(NICKNAME) : data.getString(ID));
            CURRENT_ACCOUNT.put(NICKNAME, data.getString(NICKNAME));
            //关闭窗口
            close.getScene().getWindow().fireEvent(new Event(WindowEvent.WINDOW_CLOSE_REQUEST));
            //好友列表
            var friend = new JsonObject().put(FRIENDS, data.getJsonArray(FRIENDS));
            //跳转到主页面
            new MainPage(friend);

            return;
        }
        Notifications.create().position(Pos.CENTER).text("登陆失败,请检查用户名/密码").showInformation();

    }

}
