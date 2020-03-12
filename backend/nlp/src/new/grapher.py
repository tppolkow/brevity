import networkx as nx
import matplotlib
import gc
from memory_profiler import profile
matplotlib.use("Agg")


class Grapher:
    @staticmethod
    @profile
    def graph(g_mat):
        graph = nx.Graph(g_mat)
        pagerank = nx.pagerank(graph)
        #edge_labels = nx.get_edge_attributes(graph, 'weight')
        #pos = nx.spring_layout(graph)
        #nx.draw(graph, pos, with_labels=True)
        #nx.draw_networkx_edge_labels(graph, pos, edge_labels=edge_labels)
        # plt.show()

        del graph

        return pagerank
