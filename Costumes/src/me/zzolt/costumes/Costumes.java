package me.zzolt.costumes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import cn.nukkit.entity.data.Skin;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.plugin.PluginBase;

public class Costumes extends PluginBase {
	
	private static Costumes instance;
	public Path skinFolder;
	
	@Override
	public void onEnable() {
		instance = this;
		if(!Files.exists(this.getDataFolder().toPath())) {
			try {
				Files.createDirectories(Paths.get(this.getDataFolder() + "/skins/"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		skinFolder = Paths.get(this.getDataFolder() + "/skins/");
		this.getServer().getPluginManager().registerEvents(new EventListener(), this);
	}
	
	@Override
	public void onDisable() {
		
	}
	
	public static Costumes getInstance() {
		return instance;
	}
	
	public FormWindowSimple formFromPath(Path p) throws IOException {
		
		Stream<Path> paths = Files.walk(p, 1);
		List<String> collect = paths.map(String::valueOf).sorted().collect(Collectors.toList());
		FormWindowSimple fws = new FormWindowSimple("Costume", p.toString());
		if(!p.toString().endsWith("skins\\geometry")) {
			fws.addButton(new ElementButton(".."));
		}
		collect.remove(0);
		for(String path : collect) {
			fws.addButton(new ElementButton(path.substring(1 + path.lastIndexOf("\\"))));
		}
		paths.close();
		return fws;
	}
	
	public Skin getSkin(Path path) throws IOException {
		Skin skin = new Skin();
		skin.generateSkinId(path.toString().substring(1 + path.toString().lastIndexOf("\\")));
		skin.setGeometryName(path.toString().replace("\\", ".").substring(path.toString().indexOf("geometry")));
		skin.setSkinResourcePatch("{\"geometry\":{\"default\":\"" + path.toString().replace("\\", ".").substring(path.toString().indexOf("geometry")) + "\"}}");
		skin.setGeometryData(new String(Files.readAllBytes(Paths.get(path.toString() + "/geometry.json"))));
		skin.setSkinData(ImageIO.read(Paths.get(path.toString() + "/skin.png").toFile()));
		System.out.println(skin.getSkinResourcePatch());
		return skin;
	}

}
