/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package h2pagestorevisualizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 *
 * @author ayataro
 */
public class Topic {
    private static Topic instance = new Topic();
    private static Map<String, List<Consumer<Map<String, Object>>>> subscriber = new HashMap<String, List<Consumer<Map<String, Object>>>>();
    private Topic(){
        
    }
    public static Topic getInstance(){
        return instance;
    }
    
    public void publish(String topic, Map<String, Object> param){
        List<Consumer<Map<String, Object>>> list = subscriber.get(topic);
        if(list == null){
            list = new ArrayList<>();
        }
        list.forEach(s -> s.accept(param));
    }
    
    public void subscribe(String topic, Consumer<Map<String, Object>> func){
        List<Consumer<Map<String, Object>>> list = subscriber.get(topic);
        if(list == null){
            list = new ArrayList<>();
            subscriber.put(topic, list);
        }
        list.add(func);
    }
}
