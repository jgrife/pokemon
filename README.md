# Pokemon

## Functionality

1. The MainActivity displays a paginated (utilizing Paging3) list of all Pokemon on startup. DONE
2. You can perform a Search for a Pokemon by name, from the top search bar on the MainActivity. The results from this too, are paginated. DONE
3. When a Pokemon is selected, we show a detailed screen view. DONE
4. Showing a ProgressBar, when the list of Pokemon is first being downloaded over the network. DONE
5. Showing an error message and Retry button, when getting the list of Pokemon from over the network, fails. DONE
6. Added Unit Tests for PokemonRepositoryTest. DONE

### Thoughts

- The https://pokeapi.co/api/v2/pokemon endpoint doesn't return an image url for each Pokemon. So I have a hack in place to resolve for the image in the Pokemon model 'imageUrl'. The fix would be to either update the API (which I have no control over), to return the imageUrl. Or I would have to make a separate request on the list screen, for each and ever Pokemon for their details, which does have the imageUrl. For obvious reasons, I didn't go with this approach.

- That same endpoint also doesn't return an ID for each Pokemon. But I have this ID being parsed from the Pokemon#url property. Not great, but a necessary requirement for Room entities to have a PrimaryKey. Also, an ID comes in handy in other places as well. Again the fix for this would be the same as listed in the previous bullet point. 