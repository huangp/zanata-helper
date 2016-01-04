package org.zanata.helper.service.impl;

import org.scannotation.AnnotationDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zanata.helper.common.annotation.RepoPlugin;
import org.zanata.helper.common.annotation.TranslationServerPlugin;
import org.zanata.helper.common.plugin.RepoExecutor;
import org.zanata.helper.common.plugin.TranslationServerExecutor;
import org.zanata.helper.exception.UnableLoadPluginException;
import org.zanata.helper.service.PluginsService;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
@Service
public final class PluginsServiceImpl implements PluginsService {
    private static final Logger log =
            LoggerFactory.getLogger(PluginsServiceImpl.class);
    @Autowired
    private ServletContext servletContext;

    private final static Map<String, Class<? extends RepoExecutor>>
        sourceRepoPluginMap =
        new HashMap<String, Class<? extends RepoExecutor>>();

    private final static Map<String, Class<? extends TranslationServerExecutor>>
        transServerPluginMap =
        new HashMap<String, Class<? extends TranslationServerExecutor>>();

    /**
     * Initiate all plugins available
     */
    @PostConstruct
    public void initialise() {
        Set<String> libJars = servletContext.getResourcePaths("/WEB-INF/lib");
        Set<URL> pluginJars = libJars.stream()
                .filter(jar -> jar.toLowerCase().contains("plugin") ||
                        jar.toLowerCase().contains("common"))
                .map(jar -> {
                    try {
                        return servletContext.getResource(jar);
                    } catch (MalformedURLException e) {
                        log.error("error getting resource", e);
                        return null;
                    }
                })
                .filter(url -> url != null)
                .collect(Collectors.toSet());

        AnnotationDB db = new AnnotationDB();
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            URL[] urls = pluginJars.toArray(new URL[]{});
            db.scanArchives(urls);
            Set<String> repoPluginClasses =
                    db.getAnnotationIndex().get(RepoPlugin.class.getName());
            log.info("available repo plugins - {}", repoPluginClasses);

            for (String cls : repoPluginClasses) {
                Class<? extends RepoExecutor> entity =
                    (Class<? extends RepoExecutor>) cl.loadClass(cls);
                sourceRepoPluginMap.put(entity.getName(), entity);
            }

            Set<String> transServerPluginClasses =
                    db.getAnnotationIndex()
                            .get(TranslationServerPlugin.class.getName());
            log.info("available translation server plugins - {}",
                    transServerPluginClasses);

            for (String cls : transServerPluginClasses) {
                Class<? extends TranslationServerExecutor> entity =
                        (Class<? extends TranslationServerExecutor>) cl
                                .loadClass(cls);
                transServerPluginMap.put(entity.getName(), entity);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<RepoExecutor> getAvailableSourceRepoPlugins() {
        List<RepoExecutor> result = new ArrayList<>();
        for (Class plugin : sourceRepoPluginMap.values()) {
            try {
                RepoExecutor executor =
                    getNewSourceRepoPlugin(plugin.getName(), null);
                result.add(executor);
            } catch (UnableLoadPluginException e) {
                log.warn("Unable to load plugin " + e.getMessage());
            }
        }
        return result;
    }

    @Override
    public List<TranslationServerExecutor> getAvailableTransServerPlugins() {
        List<TranslationServerExecutor> result = new ArrayList<>();
        for (Class plugin : transServerPluginMap.values()) {
            try {
                TranslationServerExecutor executor =
                    getNewTransServerPlugin(plugin.getName(), null);
                result.add(executor);
            } catch (UnableLoadPluginException e) {
                log.warn("Unable to load plugin " + e.getMessage());
            }
        }
        return result;
    }

    @Override
    public RepoExecutor getNewSourceRepoPlugin(String className) {
        for (Class plugin : sourceRepoPluginMap.values()) {
            if (plugin.getClass().getName().equals(className)) {
                try {
                    return getNewSourceRepoPlugin(plugin.getName(), null);
                } catch (UnableLoadPluginException e) {
                    log.warn("Unable to load plugin " + e.getMessage());
                }
            }
        }
        return null;
    }

    @Override
    public TranslationServerExecutor getNewTransServerPlugin(String className) {
        for (Class plugin : transServerPluginMap.values()) {
            if (plugin.getClass().getName().equals(className)) {
                try {
                    return getNewTransServerPlugin(plugin.getName(), null);
                } catch (UnableLoadPluginException e) {
                    log.warn("Unable to load plugin " + e.getMessage());
                }
            }
        }
        return null;
    }

    @Override
    public RepoExecutor getNewSourceRepoPlugin(String className,
        Map<String, String> fields) throws UnableLoadPluginException {
        Class<? extends RepoExecutor>
            executor = sourceRepoPluginMap.get(className);
        try {
            return executor.getDeclaredConstructor(Map.class)
                .newInstance(fields);
        } catch (Exception e) {
            e.printStackTrace();
            throw new UnableLoadPluginException(className);
        }
    }

    @Override
    public TranslationServerExecutor getNewTransServerPlugin(
        String className,
        Map<String, String> fields) throws UnableLoadPluginException {
        Class<? extends TranslationServerExecutor>
            executor = transServerPluginMap.get(className);
        try {
            return executor.getDeclaredConstructor(Map.class)
                .newInstance(fields);
        } catch (Exception e) {
            e.printStackTrace();
            throw new UnableLoadPluginException(className);
        }
    }

//    public static void main(String args[]) {
//        System.out.println(org.zanata.helper.plugin.git.Plugin.class.getName());
////        sourceRepoPluginMap.put("gitPlugin",
////            org.zanata.helper.plugin.git.Plugin.class);
////        SourceRepoExecutor executor = getNewSourceRepoPlugin("gitPlugin", null);
//    }
}
