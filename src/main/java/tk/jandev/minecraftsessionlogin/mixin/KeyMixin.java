package tk.jandev.minecraftsessionlogin.mixin;

// added by Eposs

import net.minecraft.network.encryption.PlayerKeyPair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tk.jandev.minecraftsessionlogin.client.sessionLogin.GetPublicKey;
import tk.jandev.minecraftsessionlogin.client.sessionLogin.SetSession;

@Mixin(PlayerKeyPair.class)
public class KeyMixin {

    @Inject(at=@At("TAIL"), method="privateKey", cancellable = true)
    private void privateKey(CallbackInfoReturnable cir) {
        if (SetSession.originalSession) return;
        cir.setReturnValue(GetPublicKey.privateKey);
    }

    @Inject(at=@At("TAIL"), method="publicKey", cancellable = true)
    private void publicKey(CallbackInfoReturnable cir) {
        if (SetSession.originalSession) return;
        cir.setReturnValue(GetPublicKey.publicKeyData);
    }

    @Inject(at=@At("TAIL"), method="refreshedAfter", cancellable = true)
    private void refreshedAfter(CallbackInfoReturnable cir) {
        if (SetSession.originalSession) return;
        cir.setReturnValue(GetPublicKey.refreshedAfter);
    }

/*
@Mixin(PlayerPublicKey.PublicKeyData.class)
...
    @Inject(at=@At("TAIL"), method="key", cancellable = true)
    private void key(CallbackInfoReturnable cir) {
        if (SetSession.originalSession) return;
        cir.setReturnValue(GetPublicKey.publicKey); // PublicKey
    }

    @Inject(at=@At("TAIL"), method="expiresAt", cancellable = true)
    private void expiresAt(CallbackInfoReturnable cir) {
        if (SetSession.originalSession) return;
        cir.setReturnValue(GetPublicKey.expiresAt); // expiresAt Time
    }

    @Inject(at=@At("TAIL"), method="keySignature", cancellable = true)
    private void keySignature(CallbackInfoReturnable cir) {
        if (SetSession.originalSession) return;
        cir.setReturnValue(GetPublicKey.keySignature); // keySignature
    }
*/
}
