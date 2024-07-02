# On / Off

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
   ![](assets/upload_6ba8ce5b3c77c331b55d85451df369e7.png)

4. Modifying the flag key to `hello-world-enabled` which is used as identifier in program.
   ![](assets/upload_8f7e3ea5fc37c02413b944aa24aaa222.png)

5. Clicking the button `Save Flag` to ensure the key has been modified.
   ![](assets/upload_88e1a7f7f29a0d46e74ff4eccd57f503.png)

6. Creating two `Variant`s with keys `on` and `off`.
   > Variant: the possible result for the feature toggle.

![](assets/upload_966e9460058fc102db48992223601e11.png)

7. Clicking button `New Segment` and creating a `Segment` with `100%` `Rollout`.
   > Segment: a specific subset of your user base or audience.
   > Rollout: gradually exposing a new feature to a broader audience over time.

![](assets/upload_1838bc43390bd5273c062a685757d3ed.png)
![](assets/upload_2f5e2453c67331d8ce638bac9a405f78.png)

8. Clicking button `edit` to edit `Distribution`, click checkbox to enable distribution for variant key `on` and set distribution rate to `100%`.
   > Distribution: the distribution rate for specific variants.

![](assets/upload_81bbcf7ad8bd1251eb884bea24deae46.png)
![](assets/upload_719dbcb0d95e6febe676f22ad5b65a73.png)

9. Enable the feature toggle
   ![](assets/upload_06c7b529d1f20bc085abbfa0086479ba.png)

### Running the demo App

```shell
# cd to project root
./gradlew -q --console plain :examples:openfeature-example:run
```

![](assets/upload_c199904bf367cf7bd408b2d5c7ed8bf7.gif)
