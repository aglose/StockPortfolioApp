package com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.andrew.androiddevelopment.stockportfolioapp.R;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.adapters.RegularNewsCardAdapter;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.downloadertasks.RegularNewsDownloaderTask;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.items.RegularNewsItem;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.managers.RegularNewsManager;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.view.ExpandableCard;
import com.andrew.androiddevelopment.stockportfolioapp.com.andrew.stockapp.view.RegularNewsCard;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.ViewToClickToExpand;
import it.gmariotti.cardslib.library.view.CardListView;

/**
 * Created by Andrew on 4/1/2015.
 */

public class RegularNewsFragment extends Fragment {
    private RecyclerView regularNewsList;
    private RegularNewsCardAdapter regularNewsCardAdapter;
    private RegularNewsManager regularNewsManager = new RegularNewsManager();
    private SwipeRefreshLayout swipeRefreshLayout;
    private View rootView;
    private CardListView listView;
    boolean initialLoad = false;

    public RegularNewsFragment newInstance(boolean initialLoad) {
        RegularNewsFragment fragment = new RegularNewsFragment();

        Bundle args = new Bundle();
        args.putBoolean("Initial", initialLoad);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_regular_news_recycler, container, false);

        return rootView;
    }

    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });
        RegularNewsDownloaderTask regularNewsDownloaderTask = new RegularNewsDownloaderTask(getActivity(), regularNewsManager, rootView, true);

        regularNewsDownloaderTask.execute();
    }

    public void initCards(RegularNewsManager regularNewsManager) {

        this.regularNewsManager = regularNewsManager;

        ArrayList<Card> cards = new ArrayList<Card>();
        for (int i=0;i<regularNewsManager.getCount();i++){
            RegularNewsItem newsItem = regularNewsManager.getRegularNewsItem(i);

            Card card = createExpandableCard(newsItem.getArticleTitle(),i);
            cards.add(card);
        }

        CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(getActivity(),cards);

        listView = (CardListView) getActivity().findViewById(R.id.list_expand_view);
        if (listView!=null){
            listView.setAdapter(mCardArrayAdapter);
        }
    }

    private Card createExpandableCard(String titleHeader,int i) {
        ViewToClickToExpand viewToClickToExpand =
                ViewToClickToExpand.builder()
                        .highlightView(false)
                        .setupCardElement(ViewToClickToExpand.CardElementUI.CARD);

        RegularNewsCard card = new RegularNewsCard(getActivity(), regularNewsManager, i);
        card.setViewToClickToExpand(viewToClickToExpand);

//        CardHeader header = new CardHeader(getActivity());
//
//        header.setTitle(titleHeader);
//        header.setButtonExpandVisible(true);
//
//        card.addCardHeader(header);

        ExpandableCard expand = new ExpandableCard(getActivity(), regularNewsManager, i);

        card.addCardExpand(expand);

        card.setSwipeable(true);

        card.setOnExpandAnimatorEndListener(new Card.OnExpandAnimatorEndListener() {
            @Override
            public void onExpandEnd(Card card) {
//                Toast.makeText(getActivity(), "Expand " + card.getCardHeader().getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

        card.setOnCollapseAnimatorEndListener(new Card.OnCollapseAnimatorEndListener() {
            @Override
            public void onCollapseEnd(Card card) {
//                Toast.makeText(getActivity(),"Collpase " +card.getCardHeader().getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

        return card;
    }
    private void refreshItems() {
        listView.setVisibility(View.INVISIBLE);
        RegularNewsDownloaderTask regularNewsDownloaderTask = new RegularNewsDownloaderTask(getActivity(), regularNewsManager, rootView, true);

        regularNewsDownloaderTask.execute();
    }

}
