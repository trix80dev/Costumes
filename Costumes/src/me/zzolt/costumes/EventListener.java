package me.zzolt.costumes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerInteractEvent.Action;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.PlayerSkinPacket;
import cn.nukkit.utils.TextFormat;

public class EventListener implements Listener {
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		Player player = event.getPlayer();
		
		if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			
			if(player.getInventory().getItemInHand().getId() == Item.TOTEM) {
				
				
				Path path = Paths.get(Costumes.getInstance().skinFolder.toString() + "/geometry/");
				player.sendMessage(path.toString());
				try {
					FormWindowSimple fws = Costumes.getInstance().formFromPath(path);
					player.showFormWindow(fws);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerFormResponded(PlayerFormRespondedEvent event) throws IOException {
		
		Player player = event.getPlayer();
		
		if (event.getResponse() instanceof FormResponseSimple) {
			FormResponseSimple formResponse = (FormResponseSimple) event.getResponse();
			FormWindowSimple formWindow = (FormWindowSimple) event.getWindow();
			
			if(formWindow.getTitle() != "Costume") return;
			
			if(formResponse.getClickedButton().getText() == "..") {
				Path path = Paths.get(formWindow.getContent()).getParent();
				try {
					FormWindowSimple fws = Costumes.getInstance().formFromPath(path);
					player.showFormWindow(fws);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				Path path = Paths.get(formWindow.getContent().toString() + "\\" + formResponse.getClickedButton().getText());
				try {
					boolean f = Files.list(path).anyMatch(x -> Files.isDirectory(x));
					if (f) {
						FormWindowSimple fws = Costumes.getInstance().formFromPath(path);
						player.showFormWindow(fws);
					} else {
						player.sendMessage(path.toString().replace("\\", ".").substring(path.toString().indexOf("geometry")));
						Skin skin = Costumes.getInstance().getSkin(path);
						player.sendMessage(TextFormat.AQUA + "Swapped Skin to " + TextFormat.YELLOW + skin.getSkinId());
						
						player.setSkin(skin);
						PlayerSkinPacket psp = new PlayerSkinPacket();
						psp.oldSkinName = "Custom";
						psp.newSkinName = "Custom";
						psp.uuid = player.getUniqueId();
						
						psp.skin = skin;
						
						Server server = Server.getInstance();
						player.dataPacket(psp);
						Server.broadcastPacket(server.getOnlinePlayers().values(), psp);
						
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
