package com.example.ahn.ttubucke;

import static java.lang.Math.*;

/**
 * Created by ahn on 2018-05-19.
 */

public class Graph {
    private int n;           //노드들의 수
    private double maps[][];    //노드들간의 가중치 저장할 변수

    public Graph(int n) {
        this.n = n;
        maps = new double[n + 1][n + 1];
    }

    public void input(Object[] Vertices) {
        double x1, y1, x2, y2;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++){
                x1 = ((Vertex)Vertices[i]).vertexX;
                y1 = ((Vertex)Vertices[i]).vertexY;
                x2 = ((Vertex)Vertices[j]).vertexX;
                y2 = ((Vertex)Vertices[j]).vertexY;
                maps[i][j] = sqrt(pow(x1+(-1*x2),2)+pow(y1+(-1*y2),2));
            }
        }
    } //*******

    public void dijkstra(int v) {
        double distance[] = new double[n + 1];          //최단 거리를 저장할 변수
        boolean[] check = new boolean[n + 1];     //해당 노드를 방문했는지 체크할 변수

        //distance값 초기화.
        for (int i = 1; i < n + 1; i++) {
            distance[i] = Integer.MAX_VALUE;
        }

        //시작노드값 초기화.
        distance[v] = 0;
        check[v] = true;

        //연결노드 distance갱신
        for (int i = 1; i < n + 1; i++) {
            if (!check[i] && maps[v][i] != 0) {
                distance[i] = maps[v][i];
            }
        }


        for (int a = 0; a < n - 1; a++) {
            //원래는 모든 노드가 true될때까지 인데
            //노드가 n개 있을 때 다익스트라를 위해서 반복수는 n-1번이면 된다.
            //원하지 않으면 각각의 노드가 모두 true인지 확인하는 식으로 구현해도 된다.
            double min = Integer.MAX_VALUE;
            int min_index = -1;

            //최소값 찾기
            for (int i = 1; i < n + 1; i++) {
                if (!check[i] && distance[i] != Integer.MAX_VALUE) {
                    if (distance[i] < min) {
                        min = distance[i];
                        min_index = i;
                    }
                }
            }

            check[min_index] = true;
            for (int i = 1; i < n + 1; i++) {
                if (!check[i] && maps[min_index][i] != 0) {
                    if (distance[i] > distance[min_index] + maps[min_index][i]) {
                        distance[i] = distance[min_index] + maps[min_index][i];
                    }
                }
            }

        }

        //결과값 출력
        for (int i = 1; i < n + 1; i++) {
            System.out.print(distance[i] + " ");
        }
        System.out.println("");

    }
}