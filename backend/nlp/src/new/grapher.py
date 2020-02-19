import matplotlib.pyplot as plt
import networkx as nx
import numpy as np


class Grapher:
    @staticmethod
    def graph(g_mat):
        graph = nx.Graph(g_mat)
        pagerank = nx.pagerank(graph)
        edge_labels = nx.get_edge_attributes(graph, 'weight')
        pos = nx.spring_layout(graph)
        nx.draw(graph, pos, with_labels=True)
        nx.draw_networkx_edge_labels(graph, pos, edge_labels=edge_labels)
        # plt.show()

        return pagerank
