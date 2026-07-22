package com.loficostudios.forgified.paper.items;


import com.loficostudios.forgified.paper.IPluginResources;

///TODO Rename to deferred registry
public interface DeferredRegistry<T> {
    void register(IPluginResources resources);
}
