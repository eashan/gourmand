package com.codingc.team26.restofinder.card;

import android.view.View;
import android.view.ViewGroup;

public interface IAdapterViewInflater<T>
{
	public View inflate(BaseInflaterAdapter<T> adapter, int pos, View convertView, ViewGroup parent);
}
