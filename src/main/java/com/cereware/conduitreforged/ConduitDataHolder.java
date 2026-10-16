package com.cereware.conduitreforged;

import org.jspecify.annotations.Nullable;

/** Implementado por ConduitBlockEntity (vía mixin) para conservar los datos del conduit colocado. */
public interface ConduitDataHolder {
	@Nullable ConduitData conduitreforged$getData();

	void conduitreforged$setData(@Nullable ConduitData data);
}
