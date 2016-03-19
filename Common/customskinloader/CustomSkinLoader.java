package customskinloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import customskinloader.config.Config;
import customskinloader.config.SkinSiteProfile;
import customskinloader.loader.CustomSkinAPILoader;
import customskinloader.loader.IProfileLoader;
import customskinloader.loader.LegacyLoader;
import customskinloader.loader.MojangAPILoader;
import customskinloader.loader.UniSkinAPILoader;
import net.minecraft.client.Minecraft;

/**
 * Custom skin loader mod for Minecraft.
 * @author (C) Jeremy Lam [JLChnToZ] 2013 & Alexander Xia [xfl03] 2014-2016
 * @version 13.3 (2016.3.13)
 */
public class CustomSkinLoader {
	public static final String CustomSkinLoader_VERSION="13.3";
	public static final File DATA_DIR=new File(Minecraft.getMinecraft().mcDataDir,"CustomSkinLoader"),
            LOG_FILE=new File(DATA_DIR,"CustomSkinLoader.log"),
            CONFIG_FILE=new File(DATA_DIR,"CustomSkinLoader.json");
	public static final SkinSiteProfile[] DEFAULT_LOAD_LIST={
			new SkinSiteProfile("Mojang","MojangAPI"),
			new SkinSiteProfile("BlessingSkin","CustomSkinAPI","https://skin.prinzeugen.net/csl/"),
			//Minecrack could not load skin correctly
			//new SkinSiteProfile("Minecrack","Legacy","http://minecrack.fr.nf/mc/skinsminecrackd/{USERNAME}.png","http://minecrack.fr.nf/mc/cloaksminecrackd/{USERNAME}.png"),
			new SkinSiteProfile("SkinMe","UniSkinAPI","http://www.skinme.cc/uniskin/")};
	public static final HashMap<String,IProfileLoader> LOADERS=initLoaders();
	
	public static final Logger logger=initLogger();
	public static final Config config=loadConfig0();
	
	public static Map loadProfile(String username,Map defaultProfile){
		for(int i=0;i<config.loadlist.length;i++){
			SkinSiteProfile ssp=config.loadlist[i];
			logger.info((i+1)+"/"+config.loadlist.length+" Try to load profile from '"+ssp.name+"'.");
			if(ssp.type.equalsIgnoreCase("MojangAPI") && !defaultProfile.isEmpty()){
				logger.info("Default profile will be used.");
				logger.info(ModelManager0.toUserProfile(defaultProfile).toString());
				return defaultProfile;
			}
			IProfileLoader loader=LOADERS.get(ssp.type.toLowerCase());
			if(loader==null){
				logger.info("Type '"+ssp.type+"' is not defined.");
				continue;
			}
			UserProfile profile=null;
			try{
				profile=loader.loadProfile(ssp, username);
			}catch(Exception e){
				logger.info("Exception occurs while loading: "+e.getMessage());
			}
			if(profile==null)
				continue;
			logger.info(username+"'s profile loaded.");
			logger.info(profile.toString());
			return ModelManager0.fromUserProfile(profile);
		}
		return defaultProfile;
	}
	
	private static Logger initLogger() {
		Logger logger=new Logger(LOG_FILE);
		logger.info("CustomSkinLoader "+CustomSkinLoader_VERSION);
		logger.info("Log File: "+LOG_FILE.getAbsolutePath());
		return logger;
	}

	private static HashMap<String, IProfileLoader> initLoaders() {
		HashMap<String, IProfileLoader> loaders=new HashMap<String, IProfileLoader>();
		loaders.put("mojangapi", new MojangAPILoader());
		loaders.put("customskinapi", new CustomSkinAPILoader());
		loaders.put("legacy", new LegacyLoader());
		loaders.put("uniskinapi", new UniSkinAPILoader());
		return loaders;
	}

	private static Config loadConfig0() {
		Config config=loadConfig();
		logger.info("Enable: "+config.enable);
		logger.info("LoadList: "+(config.loadlist==null?0:config.loadlist.length));
		return config;
	}

	private static Config loadConfig() {
		logger.info("Config File: "+CONFIG_FILE.getAbsolutePath());
		if(!CONFIG_FILE.exists()){
			logger.info("Config file not found, use default instead.");
			return initConfig();
		}
		try {
			logger.info("Try to load config.");
			String json=IOUtils.toString(new FileInputStream(CONFIG_FILE));
			Config config=new Gson().fromJson(json, Config.class);
			logger.info("Successfully load config.");
			return config;
		}catch (Exception e) {
			logger.info("Failed to load config, use default instead.("+e.getMessage()+")");
			return initConfig();
		}
	}

	private static Config initConfig() {
		Config config=new Config(DEFAULT_LOAD_LIST);
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json=gson.toJson(config);
		if(CONFIG_FILE.exists())
			CONFIG_FILE.delete();
		try {
			CONFIG_FILE.createNewFile();
			IOUtils.write(json, new FileOutputStream(CONFIG_FILE));
			logger.info("Successfully create config.");
		} catch (Exception e) {
			logger.info("Failed to create config.("+e.getMessage()+")");
		}
		return config;
	}
}
