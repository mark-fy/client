package wtf.capes.sim;

import wtf.capes.utils.Mth;
import wtf.tophat.Client;
import wtf.tophat.modules.impl.render.CustomCape;

import java.util.ArrayList;
import java.util.List;

public class StickSimulation {
	public List<Point> points = new ArrayList<>();

	public List<Stick> sticks = new ArrayList<>();

	public float gravity = 20.0F;

	public int numIterations = 30;

	public void simulate() {
		if(Client.moduleManager.getByClass(CustomCape.class).isEnabled()) {
			this.gravity = Client.moduleManager.getByClass(CustomCape.class).gravity.get().floatValue();
		} else {
			this.gravity = 32;
		}

		float deltaTime = 0.05F;
		Vector2 down = new Vector2(0.0F, this.gravity * deltaTime);
		Vector2 tmp = new Vector2(0.0F, 0.0F);
		for (Point p : this.points) {
			if (!p.locked) {
				tmp.copy(p.position);
				p.position.subtract(down);
				p.prevPosition.copy(tmp);
			}
		}
		Point basePoint = this.points.get(0);
		for (Point p : this.points) {
			if (p != basePoint && p.position.x - basePoint.position.x > 0.0F)
				basePoint.position.x -= 0.1F;
		}
		int i;
		for (i = this.points.size() - 2; i >= 1; i--) {
			double angle = getAngle(this.points.get(i).position, this.points.get(i - 1).position,
					this.points.get(i + 1).position);
			angle *= 57.2958D;
			if (angle > 360.0D)
				angle -= 360.0D;
			if (angle < -360.0D)
				angle += 360.0D;
			double abs = Math.abs(angle);
			float maxBend = 5.0F;
			if (abs < (180.0F - maxBend)) {
				this.points.get(i + 1).position = getReplacement(this.points.get(i).position,
						this.points.get(i - 1).position, angle, (180.0F - maxBend + 1.0F));
			}
			if (abs > (180.0F + maxBend)) {
				this.points.get(i + 1).position = getReplacement(this.points.get(i).position,
						this.points.get(i - 1).position, angle, (180.0F + maxBend - 1.0F));
			}
		}
		for (i = 0; i < this.numIterations; i++) {
			for (int j = this.sticks.size() - 1; j >= 0; j--) {
				Stick stick = this.sticks.get(j);
				Vector2 stickCentre = stick.pointA.position.clone().add(stick.pointB.position).div(2.0F);
				Vector2 stickDir = stick.pointA.position.clone().subtract(stick.pointB.position).normalize();
				if (!stick.pointA.locked)
					stick.pointA.position = stickCentre.clone().add(stickDir.clone().mul(stick.length / 2.0F));
				if (!stick.pointB.locked)
					stick.pointB.position = stickCentre.clone().subtract(stickDir.clone().mul(stick.length / 2.0F));
			}
		}
		for (Stick stick : this.sticks) {
			Vector2 stickDir = stick.pointA.position.clone().subtract(stick.pointB.position).normalize();
			if (!stick.pointB.locked)
				stick.pointB.position = stick.pointA.position.clone().subtract(stickDir.mul(stick.length));
		}
	}

	private Vector2 getReplacement(Vector2 middle, Vector2 prev, double angle, double target) {
		double theta = target / 57.2958D;
		float x = prev.x - middle.x;
		float y = prev.y - middle.y;
		if (angle < 0.0D)
			theta *= -1.0D;
		double cs = Math.cos(theta);
		double sn = Math.sin(theta);
		return new Vector2((float) (x * cs - y * sn + middle.x), (float) (x * sn + y * cs + middle.y));
	}

	private double getAngle(Vector2 middle, Vector2 prev, Vector2 next) {
		return Math.atan2((next.y - middle.y), (next.x - middle.x))
				- Math.atan2((prev.y - middle.y), (prev.x - middle.x));
	}

	public static class Point {
		public Vector2 position = new Vector2(0.0F, 0.0F);

		public Vector2 prevPosition = new Vector2(0.0F, 0.0F);

		public boolean locked;

		public float getLerpX(float delta) {
			return Mth.lerp(delta, this.prevPosition.x, this.position.x);
		}

		public float getLerpY(float delta) {
			return Mth.lerp(delta, this.prevPosition.y, this.position.y);
		}
	}

	public static class Stick {
		public Point pointA;

		public Point pointB;

		public float length;

		public Stick(Point pointA, Point pointB, float length) {
			this.pointA = pointA;
			this.pointB = pointB;
			this.length = length;
		}
	}

	public static class Vector2 {
		public float x;

		public float y;

		public Vector2(float x, float y) {
			this.x = x;
			this.y = y;
		}

		public Vector2 clone() {
			return new Vector2(this.x, this.y);
		}

		public void copy(Vector2 vec) {
			this.x = vec.x;
			this.y = vec.y;
		}

		public Vector2 add(Vector2 vec) {
			this.x += vec.x;
			this.y += vec.y;
			return this;
		}

		public Vector2 subtract(Vector2 vec) {
			this.x -= vec.x;
			this.y -= vec.y;
			return this;
		}

		public Vector2 div(float amount) {
			this.x /= amount;
			this.y /= amount;
			return this;
		}

		public Vector2 mul(float amount) {
			this.x *= amount;
			this.y *= amount;
			return this;
		}

		public Vector2 normalize() {
			float f = (float) Math.sqrt((this.x * this.x + this.y * this.y));
			if (f < 1.0E-4F) {
				this.x = 0.0F;
				this.y = 0.0F;
			} else {
				this.x /= f;
				this.y /= f;
			}
			return this;
		}

		public String toString() {
			return "Vector2 [x=" + this.x + ", y=" + this.y + "]";
		}
	}
}
