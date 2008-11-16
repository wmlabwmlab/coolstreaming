package se.peertv.peertvsim.core;

public class Reflection {

	private static int executingNode = -1;

	public static void setExecutingNode(int dest) {
		executingNode = dest;
	}

	public static int getExecutingNode() {
		return executingNode;
	}

	public static String getExecutingGroup() {
		return Thread.currentThread().getName();
	}

	public static void setExecutingGroup(String executingGroup) {
		Thread.currentThread().setName(executingGroup);
	}

}
