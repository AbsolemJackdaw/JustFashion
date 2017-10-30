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
assets/fashion/textures/fashionpack/  "FashionPackName"  /  (hats/body/pants/boots)  /  "myTexture.png"

__Step 3__ : Make a pack.mcmeta  
put it in next to the assets folder.  
your pack.mcmeta will look something like this.  
![](http://i.imgur.com/F4sOd3j.png)

__Step 4__ : Make a fashionpack.json
put it next to the textures folder

in the json file, 
* you'll give the pack a unique name.
* you'll need to give every texture name per category
    * (if you don't make textures for a certain category, you can leave it out)  
    
  
Your fashionpack.json might look something like this :  
![](http://i.imgur.com/rIJLrps.png)  


# 0.1.1.0 additions : Shields and Weapons
You will need to add an extra directory to your assets/fashion folder.
it can now contain the 'textures' folder and a 'model' folder.

![](http://i.imgur.com/d9NBesm.png)  

the structure for the model folder is the same as the texture folder.

the model folder will be scanned for json files. these can be any personal rendered item, or a simple imitation of a minecraft item.

here's a sample of a simple json file that will look a carrot :
Make sure the parent is an instance of item/handheld if you're looking to use regular items. this way it will be rendered like a sword, or tool.

`{  
    "parent": "item/handheld",  
    "textures": {  
        "layer0": "items/carrot"   
    }    
}  `


if you wish to make a custom texture for your item, ""layer0": "items/carrot"" can be substituted by the path of your texture (that you will have to add to the texture folder in your zip)
here's a sample of the texture path from the base pack's shield model :
``""layer0": "fashion:fashionpack/fashionbase/shields/shield_texture""``

to make sure your model and textures get added to the game, list them in the fashionpack.json !!  
![](http://i.imgur.com/IS2C4D9.png)


### Make sure to put all of this in a zip file !


__If any of this wasn't clear enough, you can get a copy of a FashionPack [here](https://minecraft.curseforge.com/projects/justfashion-resource-pack)__
