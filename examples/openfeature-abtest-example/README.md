# A / B Testing

## Getting Started

### Hosting OpenFlagr Server via Docker Compose

```shell
# cd to project root
docker compose up
```

### Setting feature toggle

1. Opening OpenFlagr admin dashboard [http://localhost:18000/](http://localhost:18000/) in browser.

2. Entering any flag description in field which contains hint `Specific new flag description`.
   ![](assets/upload_c4ff4fe526b9ac2f8a78b224df800f9b.png)

3. Clicking the button `Create New Flag`.
   ![](assets/upload_b23eefd585567e79a95e82f6deb3f1b6.png)

4. Modifying the flag key to `blue-green-exp` which is used as identifier in program.
   ![](assets/upload_ca18b14608b59899f7bf3088707a4b32.png)

5. Clicking the button `Save Flag` to ensure the key has been modified.
   ![](assets/upload_506e52b0aea1373d5754a75702696e21.png)

6. Creating two `Variant`s with keys `blue` and `green`.
   > Variant: the possible result for the feature toggle.

![](assets/upload_966e9460058fc102db48992223601e11.png)

7. Clicking button `New Segment` and creating a `Segment` with `100%` `Rollout`.
   > Segment: a specific subset of your user base or audience.
   > Rollout: gradually exposing a new feature to a broader audience over time.

![](assets/upload_1838bc43390bd5273c062a685757d3ed.png)
![](assets/upload_2f5e2453c67331d8ce638bac9a405f78.png)

8. Clicking button `edit` to edit `Distribution`, click checkbox to enable distribution for variant keys `blue` and `green` set distribution rate to `50%` for each.
   > Distribution: the distribution rate for specific variants.

![](assets/upload_265c2a18df4cb3954450d1968c27bf93.png)
![](assets/upload_d2625ac65f669ddad92239e7f4ee7527.png)

9. Enable the feature toggle
   ![](assets/upload_17fd334aea49f23cc9b2db0b61fea01b.png)

### Running the demo App

```shell
# cd to project root
./gradlew -q --console plain :examples:openfeature-abtest-example:run
```

![](assets/upload_bd0a86e851a5567e103d5b8ece5073d9.gif)
