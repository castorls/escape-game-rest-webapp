package com.castorls.escapegame;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.castorls.escapegame.mastermind.MasterMindService;
import com.castorls.escapegame.morse.MorseService;
import com.castorls.escapegame.textcode.TextCodeService;
import com.castorls.escapegame.timer.TimerService;

@ApplicationPath("/")
public class RestApplication extends Application {

  private Config config;

  public RestApplication() {
    super();
    this.config = Config.parseFile("./config.yml");
  }

  public Config getConfig() {
    return config;
  }

  public void setConfig(Config config) {
    this.config = config;
  }

  @Override
  public Set<Class<?>> getClasses() {
    final Set<Class<?>> classes = new HashSet<Class<?>>();
    // Register my custom provider.
    classes.add(CORSFilter.class);
    // Register resources.
    return classes;
  }

  @Override
  public Set<Object> getSingletons() {
    final Set<Object> singletons = new HashSet<Object>();
    singletons.add(new SSeService());
    singletons.add(new MasterMindService());
    singletons.add(new TimerService());
    singletons.add(new MorseService());
    singletons.add(new TextCodeService());
    return singletons;
  }

  @Override
  public Map<String, Object> getProperties() {
    final Map<String, Object> properties = new HashMap<String, Object>();
    properties.put(TimerService.class.getName(),config.timer);
    properties.put(MasterMindService.class.getName(),config.mastermind);
    properties.put(MorseService.class.getName(),config.morse);
    properties.put(TextCodeService.class.getName(),config.textcode);
    return properties;
  }
}
