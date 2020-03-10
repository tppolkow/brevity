import React from 'react';
import { Button, Col, Form, Row } from 'react-bootstrap';
import { Redirect } from 'react-router-dom';
import './ChapterSelectorList.css';
import { brevityHttpPost } from './Utilities';

class ChapterSelectorList extends React.Component {
  constructor(props) {
    super(props);
    this.props = props;
    this.state = {
      goToSummary: false,
      checkedCount: 0,
      checkedChapters: {}
    };

    this.handleInputChange = this.handleInputChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  handleInputChange(e) {
    const target = e.target;
    const newCheckedCount = (target.checked) ? this.state.checkedCount + 1 : this.state.checkedCount - 1;
    const newCheckedChapter = this.state.checkedChapters;
    newCheckedChapter[target.id] = target.checked;
    this.setState({ checkedChapters: newCheckedChapter, checkedCount: newCheckedCount });
  }

  handleSubmit(e) {
    e.preventDefault();

    const selectedChapters = Object.entries(this.state.checkedChapters)
      .reduce((selected, [chapId, checked]) => {
          if (checked) {
            const id = chapId.split('-')[1];
            selected.push(this.props.location.state.data.chapters[id]);
          }
          return selected;
      }, []);

    const reqBody = {
      ...this.props.location.state.data,
      chapters: selectedChapters
    };

    brevityHttpPost("/upload/chapters", reqBody)
      .then(res => this.setState({ goToSummary: true, data: { summaryIds: res.data, fromChapterSelect: true } }));
  }

  chapterItems(chapters) {
    if (chapters === undefined) return [];

    return chapters.map((chapter, index) =>
      <div className="chapter-selector-item" key={index}>
        <Form.Check
          name={chapter.title}
          id={`chapter-${index}`}
          type="checkbox"
          label={chapter.title}
          checked={this.state.chapter}
          onChange={this.handleInputChange}
        />
      </div>
    );
  }

  render() {
    const { chapters } = this.props.location.state.data ? this.props.location.state.data : [];

    return (
      <div>
        <Row>
          <Col lg={{ span: 8, offset: 2 }}>
            <h3 className="heading">Select chapters to summarize</h3>
            <p>
              We found chapters in your PDF that can be targeted for summarization!
              <br/>
              Select the ones you would like to summarize below.
            </p>
            <Form onSubmit={this.handleSubmit}>
              {this.chapterItems(chapters)}
              <Button variant="primary" type="submit" className="submit-btn" disabled={this.state.checkedCount === 0}>Summarize</Button>
            </Form>
          </Col>
        </Row>
        {this.state.goToSummary && <Redirect to={{ pathname: "/summary", state: { data: this.state.data } }} />}
      </div>
    );
  }

}

export default ChapterSelectorList;