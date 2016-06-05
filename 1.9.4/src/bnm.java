import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;

public class bnm implements bng {
   private int[] a;//imageData
   private int b;//imageWidth
   private int c;//imageHeight

   //BufferedImage parseUserSkin(BufferedImage image)
   public BufferedImage a(BufferedImage image) {
      if(image == null) {
         return null;
      } else {
         int ratio = image.getWidth() / 64;
         this.b = 64 * ratio;
         this.c = 64 * ratio;
         BufferedImage var2 = new BufferedImage(this.b, this.c, 2);
         Graphics var3 = var2.getGraphics();
         var3.drawImage(image, 0, 0, (ImageObserver)null);
         boolean var4 = image.getHeight() == 32 * ratio;
         if(var4) {
            if(!customskinloader.CustomSkinLoader.config.enableTransparentSkin){
               var3.setColor(new Color(0, 0, 0, 0));
               var3.fillRect(0, 32 * ratio, 64 * ratio, 32 * ratio);
            }
            var3.drawImage(var2, 24 * ratio, 48 * ratio, 20 * ratio, 52 * ratio,  4 * ratio, 16 * ratio,  8 * ratio, 20 * ratio, (ImageObserver)null);
            var3.drawImage(var2, 28 * ratio, 48 * ratio, 24 * ratio, 52 * ratio,  8 * ratio, 16 * ratio, 12 * ratio, 20 * ratio, (ImageObserver)null);
            var3.drawImage(var2, 20 * ratio, 52 * ratio, 16 * ratio, 64 * ratio,  8 * ratio, 20 * ratio, 12 * ratio, 32 * ratio, (ImageObserver)null);
            var3.drawImage(var2, 24 * ratio, 52 * ratio, 20 * ratio, 64 * ratio,  4 * ratio, 20 * ratio,  8 * ratio, 32 * ratio, (ImageObserver)null);
            var3.drawImage(var2, 28 * ratio, 52 * ratio, 24 * ratio, 64 * ratio,  0 * ratio, 20 * ratio,  4 * ratio, 32 * ratio, (ImageObserver)null);
            var3.drawImage(var2, 32 * ratio, 52 * ratio, 28 * ratio, 64 * ratio, 12 * ratio, 20 * ratio, 16 * ratio, 32 * ratio, (ImageObserver)null);
            var3.drawImage(var2, 40 * ratio, 48 * ratio, 36 * ratio, 52 * ratio, 44 * ratio, 16 * ratio, 48 * ratio, 20 * ratio, (ImageObserver)null);
            var3.drawImage(var2, 44 * ratio, 48 * ratio, 40 * ratio, 52 * ratio, 48 * ratio, 16 * ratio, 52 * ratio, 20 * ratio, (ImageObserver)null);
            var3.drawImage(var2, 36 * ratio, 52 * ratio, 32 * ratio, 64 * ratio, 48 * ratio, 20 * ratio, 52 * ratio, 32 * ratio, (ImageObserver)null);
            var3.drawImage(var2, 40 * ratio, 52 * ratio, 36 * ratio, 64 * ratio, 44 * ratio, 20 * ratio, 48 * ratio, 32 * ratio, (ImageObserver)null);
            var3.drawImage(var2, 44 * ratio, 52 * ratio, 40 * ratio, 64 * ratio, 40 * ratio, 20 * ratio, 44 * ratio, 32 * ratio, (ImageObserver)null);
            var3.drawImage(var2, 48 * ratio, 52 * ratio, 44 * ratio, 64 * ratio, 52 * ratio, 20 * ratio, 56 * ratio, 32 * ratio, (ImageObserver)null);
         }

         var3.dispose();
         this.a = ((DataBufferInt)var2.getRaster().getDataBuffer()).getData();
         if(!customskinloader.CustomSkinLoader.config.enableTransparentSkin){ 
            this.b(0, 0, 32 * ratio, 16 * ratio);
            if(var4) {
               this.a(32 * ratio, 0, 64 * ratio, 32 * ratio);
            }

            this.b( 0 * ratio, 16 * ratio, 64 * ratio, 32 * ratio);
            this.b(16 * ratio, 48 * ratio, 48 * ratio, 64 * ratio);
         }
         return var2;
      }
   }

   public void a() {
   }

   //setAreaTransparent(int p_78434_1_, int p_78434_2_, int p_78434_3_, int p_78434_4_)
   private void a(int p_78434_1_, int p_78434_2_, int p_78434_3_, int p_78434_4_) {
      for(int i = p_78434_1_; i < p_78434_3_; ++i) {
         for(int j = p_78434_2_; j <  p_78434_4_; ++j) {
            int k = this.a[i + j * this.b];
            if((k >> 24 & 255) < 128) {
               return;
            }
         }
      }

      for(int i = p_78434_1_; i < p_78434_3_; ++i) {
         for(int j = p_78434_2_; j < p_78434_4_; ++j) {
            this.a[i + j * this.b] &= 16777215;
         }
      }

   }

   //setAreaOpaque(int p_78433_1_, int p_78433_2_, int p_78433_3_, int p_78433_4_)
   private void b(int p_78433_1_, int p_78433_2_, int p_78433_3_, int p_78433_4_) {
      for(int i = p_78433_1_; i < p_78433_3_; ++i) {
         for(int j = p_78433_2_; j < p_78433_4_; ++j) {
            this.a[i + j * this.b] |= -16777216;
         }
      }

   }
}