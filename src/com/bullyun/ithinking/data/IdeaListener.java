package com.bullyun.ithinking.data;

public interface IdeaListener {
    void onAddIdea(Idea parent, int index, Idea idea);
    void onDelIdea(Idea parent, Idea idea);
    void onIdeaChange(Idea idea);
    void onIdeaMove(Idea idea, Idea src, Idea dst, int index);
}
