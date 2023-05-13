# Light Map API Spec

An API for creating and updating the Light Map in RPLE.

Designed to be straightforward and provide external API for other mods to use.

## Update Loop Diagram

![Diagram of the Modular Layout](rple_light_map_diagram.svg)

## Data

- Light Map 1D: 16 strip of RGB Floats

  - The start of the strip will be dimmer than the end of the strip.
  - Sky and Block Light Maps are split into strips as they are easier to reason about.
- Light Map 2D: 16 x 16 square of RGB Integers.

  - This will be loaded into the texture at the very end.
  - The split Red/Green/Blue light maps are still RGB, but only the relevant colour channel is set.
    - E.G. the Red Light Map will have the Green and Blue byte set to 255 across all pixels.
    - This allows the multiplicative colour mixing to work both in the fixed and programmable pipelines.

## Behaviour

- Update Loop: Happens 20 times a second to refresh the Light Map textures.

  - The Light Map is sourced from the Light Map base.
  - It is passed through the Light Map masks.
  - Finally, the Light Map is split into the 3 channels and uploaded to the GPU.
- Base: supplied as a Light Map 1D, treated as the initial image.

  - This is intended to be the static layer of the Light Map, defining the base tone of the lights.
  - It may be user configurable via a ResourcePack/ShaderPack. Mods can change this, but they should prefer masks instead.
- Mask: supplied as a Light Map 1D, applied by multiplication on top of the base.

  - These can be dynamic, being layered one on top of the other with no regards to ordering.
  - Things such as the time of day, flicker, dimension/biome, potion effects or events such as red/blood moon should be applied here.
  - This is the main entry points for mods modifying the Light Map values.
- Mixed Light Map 2D: Combined Sky & Block Light Maps.

  - Once the Sky & Block Light Map 1Ds generated, they are combined into the Mixed Light Map 2D.
  - This combined Light Map used gamma corrected linear interpolation with clamping to calculate the appropriate in-between values.
  - Mixing methods are internal and not exposed by API.

## API Entry points

- Sky/Block Light Map Base

  - As these are the base Light Map, only one can be present.
  - This is decided base off a priority system applied to each source.
    - Vanilla has the lowest priority.
    - User defined has second after Vanilla.
    - API users may set their own priority values.
- Sky/Block Light Map Mask

  - Any number of these can exist and will be treated with the same respect.
  - API users are provided a list of current Light Map Masks.
    - With this information, they can decide how they should act.
      - API users cannot disable or enable masks dynamically outside of their own.
