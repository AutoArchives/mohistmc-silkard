package org.bukkit.generator;

/**
 * Represents the biome noise parameters which may be passed to a world
 * generator.
 */
public interface BiomeParameterPoint {

    /**
     * Gets the temperature of the biome at this point that is suggested by the
     * NoiseGenerator.
     *
     * @return The temperature of the biome at this point
     */
    double getTemperature();

    /**
     * Gets the humidity of the biome at this point that is suggested by the
     * NoiseGenerator.
     *
     * @return The humidity of the biome at this point
     */
    double getHumidity();

    /**
     * Gets the continentalness of the biome at this point that is suggested by
     * the NoiseGenerator.
     *
     * @return The continentalness of the biome at this point
     */
    double getContinentalness();

    /**
     * Gets the erosion of the biome at this point that is suggested by the
     * NoiseGenerator.
     *
     * @return The erosion of the biome at this point
     */
    double getErosion();

    /**
     * Gets the depth of the biome at this point that is suggested by the
     * NoiseGenerator.
     *
     * @return The depth of the biome at this point
     */
    double getDepth();

    /**
     * Gets the weirdness of the biome at this point that is suggested by the
     * NoiseGenerator.
     *
     * @return The weirdness of the biome at this point
     */
    double getWeirdness();
}
