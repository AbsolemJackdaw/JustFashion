# JustFashion

Tux'NSuits, Just Fashion for your everyday minecraft avatar !

## Fashion Packs are added just like Resourcepack, but require an additional json file.

### Creating a Fashion Pack

__Step 1__ : Create a texture.  
Hat Sample : ![](http://i.imgur.com/bU4zMSC.png)  
Body Sample : ![](http://i.imgur.com/TCzXL0i.png)  
Pants Sample : ![](http://i.imgur.com/Ys38EbL.png)  
Boots Sample : ![](http://i.imgur.com/9cOEaAo.png)

__Step 2__ : Put them in the correct location  
assets/fashion/textures/ (demopack) /  (hats/body/pants/boots)  /  "my_texture.png"

__Step 3__ : Make a pack.mcmeta  
The pac.mcmeta goes on the top level, next to the assets folder.  
your pack.mcmeta will look something like this.

```
{
  "pack":{
     "pack_format":9,
     "description":"Demo Fashion Pack" 
  }
}  
```  

__Step 4__ : Make a fashionpack.json
This json file has to be explicitly named 'fashionpack.json'
It goes inside of the fashion folder : `assets/fashion/`.  
Your pack json will now be next to the textures folder.

in the json file,

* you'll give the pack a unique name.
* you'll need to give every texture name per category
    * (if you don't make textures for a certain category, you can leave it out)
* use all lower case

Your fashionpack.json might look something like this :

```
{
    "pack": "demopack",
    "hats" : [
        "flowers.png",
        "builder.png"
    ],
    "body": [
        "sundress.png",
        "overall.png"    
    ], 
    "pants": [
        "torn.png"
    ],
    "boots": [
        "swanky_boots.png"
    ]
}
```

# 0.1.1.0 additions : Shields and Weapons

You can add cosmetic overrides to shields and swords.  
You will need to add an extra directory to your `assets/fashion/` folder.  
Apart from the pack json and ``textures`` folder, it can now also contain the ``models`` folders

![](http://i.imgur.com/d9NBesm.png)

The model folder will be scanned for json files :  

### Item Model Cosmetic Swords
You can have an item model json, which is in essence an imitation of a regular item model,  
and takes a 'handheld' parent, and an item texture, see below :

```
{  
    "parent": "item/handheld",  
    "textures": {  
        "layer0": "items/carrot"   
    }    
} 
```  

This json will make your sword look like a carrot.  

If you wish to make a custom texture for your item, `"layer0":"items/carrot"` can be substituted by the path of your
texture.  
I.E. : your ``fishbone.png`` texture that you made for a cosmetic sword,  
will have to be placed in `assets/fashion/textures/demopack/weapons/`.  
The path to provide to your json item will thus be ``"layer0":"fashion:demopack/weapons/fishbone.png"``  

### Model Cosmetic Override
Using BlockBench, or your favourite model maker for minecraft related items,  
you can export a json model.  
This model equally goes into the `assets/fashion/textures/demopack/weapons/` folder.  
If you are dealing with a shield however, put it in `assets/fashion/textures/demopack/shields/`.  

If either of these models use a custom texture (not provided by any mod no minecraft), you have to put that texture in the respective texture folder.  
``assets/fashion/textures/demopack/(weapons | shields)/``


The last step is to add the references of these new items to your pack json.  
```
{
    "pack": "demopack",
    "hats" : [
       ...
    ],
    "body": [
       ...
    ], 
    "pants": [
       ...
    ],
    "boots": [
       ...
    ],
    "weapon_models": ["item/fishbone"],
    "shield_models": []
}
```
NOTE :  
This example prefixes the ``item/`` to the model name. This is to let Just Fashion know we're effectively talking about a handheld model json.  
If you have a custom modeled item, and want to render the model as-is, do *not* append ``item/`` to the model reference name.  



### Make sure to put all of this in a zip file !

__If any of this wasn't clear enough, you can get a copy of a
FashionPack [here](https://minecraft.curseforge.com/projects/justfashion-resource-pack)__
