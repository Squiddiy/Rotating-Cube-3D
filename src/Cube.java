import java.util.Arrays;
import java.util.concurrent.TimeUnit;


public class Cube {

	public static int timesToLoop = 0;
	
	public static float a, b, c;
	public static double x, y, z;


	public static float cubeWidth = 10;
	public static int width = 40, height = 14;

	public static double[] zBuffer = new double[40 * 22];
	public static byte[] buffer = new byte[40 * 22];

	public static int backgroundAsciiCode = ' ';
	public static int distanceFromCam = 100;

	public static float horizontalOffset;
	public static float cubeSize = 40;
	public static float incrementSpeed = 0.6f;


	public static double zFraction;

	public static int xSize, ySize;

	public static int arrayIndex;

	//Anv�nder de 3 ekvationerna under "Basic rotations" p� https://en.wikipedia.org/wiki/Rotation_matrix.
	//R�knar ut v�rderna med hj�lp av symbolab d� du beh�ver bara bytta ut Ry(0) till (x,y,z) f�r att f� dina ekvationer
	private static double CalculateX(float cubeX, float cubeY, float cubeZ) {
		return cubeY * Math.sin(a) * Math.sin(b) * Math.cos(c) - cubeZ * Math.cos(a) * Math.sin(b) * Math.cos(c)
				+ cubeY * Math.cos(a) * Math.sin(c) + cubeZ * Math.sin(a) * Math.sin(c)
				+ cubeX * Math.cos(b) * Math.cos(c);
	}

	private static double CalculateY(float cubeX, float cubeY, float cubeZ) {
		return cubeY * Math.cos(a) * Math.cos(c) + cubeZ * Math.sin(a) * Math.cos(c)
				- cubeY * Math.sin(a) * Math.sin(b) * Math.sin(c) + cubeZ * Math.cos(a) * Math.sin(b) * Math.sin(c)
				- cubeX * Math.cos(b) * Math.sin(c);
	}

	private static double CalculateZ(float cubeX, float cubeY, float cubeZ) {
		return cubeZ * Math.cos(a) * Math.cos(b) - cubeY * Math.sin(a) * Math.cos(b) + cubeX * Math.sin(b);
	}

	public static void calculateForSurface(float cubeX, float cubeY, float cubeZ, int ch) {
		x = CalculateX(cubeX, cubeY, cubeZ);
		y = CalculateY(cubeX, cubeY, cubeZ);
		z = CalculateZ(cubeX, cubeY, cubeZ) + distanceFromCam;

		zFraction = 1 / z;

		xSize = (int) (width / 2 + horizontalOffset + cubeSize * zFraction * x * 2);
		ySize = (int) (height / 2 + cubeSize * zFraction * y);

		//Kollar om en del av kubben ska bli m�lad vid index.
		arrayIndex = xSize + ySize * width;
		if (arrayIndex >= 0 && arrayIndex < width * height) {
			if (zFraction > zBuffer[arrayIndex]) {
				zBuffer[arrayIndex] = zFraction;
				buffer[arrayIndex] = (byte) ch;
			}
		}
	}

	public static void main(String[] args) {
		while (timesToLoop < 100) {
					
			Arrays.fill(buffer, 0, width * height, (byte) backgroundAsciiCode);
			Arrays.fill(zBuffer, 0, width * height, (byte) 0);
			// horizontalOffset = -2 * cubeWidth;
			// first cube
			for (float cubeX = -cubeWidth; cubeX < cubeWidth; cubeX += incrementSpeed) {
				for (float cubeY = -cubeWidth; cubeY < cubeWidth; cubeY += incrementSpeed) {
					calculateForSurface(cubeX, cubeY, -cubeWidth, '@');
					calculateForSurface(cubeWidth, cubeY, cubeX, '$');
					calculateForSurface(-cubeWidth, cubeY, -cubeX, '~');
					calculateForSurface(-cubeX, cubeY, cubeWidth, '#');
					calculateForSurface(cubeX, -cubeWidth, -cubeY, ';');
					calculateForSurface(cubeX, cubeWidth, cubeY, '+');
				}
			}

			for (int k = 0; k < width * height; k++) {
				//F�rvandlar int till en char med hj�lp av bytes
				System.out.print((char) Byte.toUnsignedInt((byte) ((int) (k % width != 0 ? buffer[k] : 10))));
			}

			//Hur kuben roterar
			a += 0.05;
			b += 0.05;
			c += 0.01;

			//Pausar s� man kan se n�t
			try {
				TimeUnit.SECONDS.sleep((long) 0.3f);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally {
				//Om koden �r bygd och man k�r via cmd s� rensar detta consolen
				System.out.print("\033[H\033[2J");
				System.out.flush();
			}

		}
	}
}
