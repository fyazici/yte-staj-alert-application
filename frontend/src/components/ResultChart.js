import React, { Component } from 'react'
import axios from 'axios'
import Chart from 'react-google-charts';
import { Container } from 'react-bootstrap';

class ResultChart extends Component {
    constructor(props) {
        super(props);
        this.state = {
            resultData: [
                this.ResultChartHeader
            ]
        };
        console.log(props);
    }

    ResultChartHeader = [
        { type: "date", id: "timestamp", label: "Request Time" },
        { type: "number", id: "success", label: "Success" }
    ];

    componentDidUpdate(prevProps, prevState) {
        if (this.props !== prevProps) {
            axios.get(
                "http://localhost:8080/results/" + this.props.alertId
            ).then((resp) => {
                var newResults = resp.data.map((elem, index) => {
                    return [new Date(elem.timestamp), elem.success ? 1 : -1];
                });
                var newResultData = [this.ResultChartHeader].concat(newResults);
                console.log(newResultData);
                this.setState({resultData: newResultData});
            }).catch((err) => {
                console.error("Error on axios get: " + err);
            });
        }
    }

    render() {
        return (
            <Container className="ResultChartBox">
                <Chart
                    chartType="ColumnChart"
                    loader={<div>Loading Chart</div>}
                    data={this.state.resultData}
                    options={{
                        chart: {
                            title: "Request Results",
                        },
                        axes: {
                            x: {
                                all: {
                                    range: {
                                        min: 0
                                    }
                                }
                            },
                            y: {
                                all: {
                                    range: {
                                        max: 1,
                                        min: 0
                                    }
                                }
                            }
                        }
                    }}
                />
            </Container>
        );
    }
}

export default ResultChart;