package tk.jandev.minecraftsessionlogin.client.sessionLogin.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import tk.jandev.minecraftsessionlogin.client.sessionLogin.GetPublicKey;
import tk.jandev.minecraftsessionlogin.client.sessionLogin.SetSession;

import java.util.Objects;


public class SessionInputGui extends Screen {
    private TextFieldWidget sessionTextInputWidget;
    private TextFieldWidget usernameTextInputWidget;
    private TextFieldWidget uuidTextInputWidget;
    private ButtonWidget updateButton;
    private ButtonWidget resetButton;
    public SessionInputGui() {
        super(Text.of("session: "));
    }

    @Override
    protected void init() {
        this.sessionTextInputWidget = new TextFieldWidget(MinecraftClient.getInstance()
                .textRenderer, 20, 50, 200, 12, Text.literal("Session"));
        this.sessionTextInputWidget.setText("Input your Session ID here");
        this.sessionTextInputWidget.setFocused(false);
        this.sessionTextInputWidget.setMaxLength(50000);
        this.sessionTextInputWidget.setChangedListener((currentSession) -> System.out.println());
        this.addDrawableChild(sessionTextInputWidget);

        this.usernameTextInputWidget = new TextFieldWidget(MinecraftClient.getInstance()
                .textRenderer, 20, 30, 200, 12, Text.literal("Username"));
        this.usernameTextInputWidget.setText("Input your Username here");
        this.usernameTextInputWidget.setFocused(false);
        this.usernameTextInputWidget.setMaxLength(50000);
        this.usernameTextInputWidget.setChangedListener((currentUsername) -> System.out.println());
        this.addDrawableChild(usernameTextInputWidget);

        this.uuidTextInputWidget = new TextFieldWidget(MinecraftClient.getInstance()
                .textRenderer, 20, 10, 200, 12, Text.literal("UUID"));
        this.uuidTextInputWidget.setText("Input your FULL username's UUID here (https://mcuuid.net)");
        this.usernameTextInputWidget.setFocused(false);
        this.uuidTextInputWidget.setMaxLength(50000);
        this. uuidTextInputWidget.setChangedListener((currentUUID) -> System.out.println());
        this.addDrawableChild(uuidTextInputWidget);

        updateButton = ButtonWidget.builder(Text.of("update"), button -> {
            SetSession.UUID = uuidTextInputWidget.getText();
            SetSession.accessToken = sessionTextInputWidget.getText();
            SetSession.username = usernameTextInputWidget.getText();
            SetSession.sessionid = "token:"+SetSession.accessToken+":"+SetSession.UUID;
            MinecraftClient.getInstance().setScreen(null);
            MinecraftClient.getInstance().player.sendMessage(Text.of("§3Your session was updated! §6Relog §3to this server or §6join another one!"));
            SetSession.originalSession = false;
            try {
                GetPublicKey.requestKey();
            } catch (Exception e) {
                MinecraftClient.getInstance().player.sendMessage(Text.of("§3Unable to get Public Key! §6You wont be able to join servers that require a valid public key!"));
            }
        }).dimensions(20, 80, 40, 20).build();
        this.addDrawableChild(updateButton);

        ButtonWidget closeButton = ButtonWidget.builder(Text.of("Cancel"), button ->
                MinecraftClient.getInstance().setScreen(null)).dimensions(80, 80, 40, 20).build();
        this.addDrawableChild(closeButton);

        resetButton = ButtonWidget.builder(Text.of("reset"), button -> {
            SetSession.originalSession = true;
            MinecraftClient.getInstance().setScreen(null);
            MinecraftClient.getInstance().player.sendMessage(Text.literal("§3Your Session was reset to the one §6you started this minecraft instance with"));
        }).dimensions(140, 80, 40, 20).build();
        this.addDrawableChild(resetButton);
    }

    public void tick() {
        updateButton.active = !Objects.equals(sessionTextInputWidget.getText(), "Input your Session ID here") && !Objects.equals(sessionTextInputWidget.getText(), "");
        if (Objects.equals(usernameTextInputWidget.getText(), "Input your Username here") || Objects.equals(usernameTextInputWidget.getText(), "")) {
            updateButton.active = false;
        }

        if (Objects.equals(uuidTextInputWidget.getText(), "Input your FULL username's UUID here (https://mcuuid.net)") || Objects.equals(usernameTextInputWidget.getText(), "")) {
            updateButton.active = false;
        }
        if (resetButton.isHovered()) {
            resetButton.render(new MatrixStack(), 20, 10, 0);
        }
    }

}
