import React, { Component } from 'react'
import axios from 'axios'
import Chart from 'react-google-charts';
import { Container } from 'react-bootstrap';

class ResultChart extends Component {
    constructor(props) {
        super(props);
        this.state = {
            resultData: [
                ["Timestamp", "Success"],
                [1, 0],
                [2, 1],
                [3, 1],
                [4, 0]
            ]
        };
        console.log(props);
    }

    componentDidUpdate(prevProps, prevState) {
        axios.get(
            "http://localhost:8090/results", {
                alertId: this.props.alertId
            }
        ).then((resp) => {
            console.log(resp.data);
        }).catch((err) => {
            console.error("Error on axios get: " + err);
        })
    }

    render() {
        return (
            <Container className="ResultChartBox">
                <Chart
                    chartType="Bar"
                    loader={<div>Loading Chart</div>}
                    data={this.state.resultData}
                    options={{
                        chart: {
                            title: 'Request Results',
                        },
                    }}
                />
            </Container>
        );
    }
}

export default ResultChart;