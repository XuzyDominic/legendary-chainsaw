package com.xzydominic.xzymq.core.manager;

import com.xzydominic.xzymq.access.StaticVariableListener;

import java.util.ArrayList;
import java.util.List;

public class StaticVariableManager {

    private static Object staticVariable;

    private static final List<StaticVariableListener> listeners = new ArrayList<>();

    public static void setStaticVariable(Object value) {
        if (staticVariable != value) {
            staticVariable = value;
            notifyListeners();
        }
    }

    public static Object getMyStaticVariable() {
        return staticVariable;
    }

    public static void registerListener(StaticVariableListener listener) {
        listeners.add(listener);
    }

    public static void unregisterListener(StaticVariableListener listener) {
        listeners.remove(listener);
    }

    public static void removeAllListener() {
        listeners.clear();
    }

    private static void notifyListeners() {
        for (StaticVariableListener listener : listeners) {
            listener.staticVariableChanged(staticVariable);
        }
    }

}
